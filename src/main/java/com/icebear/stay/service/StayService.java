package com.icebear.stay.service;

import com.icebear.stay.exception.StayDeleteException;
import com.icebear.stay.exception.StayNotExistException;
import com.icebear.stay.model.*;
import com.icebear.stay.repository.LocationRepository;
import com.icebear.stay.repository.ReservationRepository;
import com.icebear.stay.repository.StayRepository;
import com.icebear.stay.repository.StayReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class StayService {
    private StayRepository stayRepository;
    private ImageStorageService imageStorageService;

    private LocationRepository locationRepository;
    private GeoCodingService geoCodingService;

    private ReservationRepository reservationRepository;
    private StayReservationDateRepository stayReservationDateRepository;



    @Autowired
    public StayService(StayRepository stayRepository,
                       ImageStorageService imageStorageService,
                       LocationRepository locationRepository,
                       GeoCodingService geoCodingService,
                       ReservationRepository reservationRepository,
                       StayReservationDateRepository stayReservationDateRepository
                       ) {
        this.stayRepository = stayRepository;
        this.imageStorageService = imageStorageService;
        this.locationRepository = locationRepository;
        this.geoCodingService = geoCodingService;
        this.reservationRepository = reservationRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
    }

    public List<Stay> listByUser(String username) {

        return stayRepository.findByHost(new User.Builder().setUsername(username).build());
    }

    public Stay findByIdAndHost(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(
                stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        return stay;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void add(Stay stay, MultipartFile[] images ) {
        // add images to google cloud storage
        List<String> mediaLinks = Arrays.stream(images).parallel().map(
                image -> imageStorageService.save(image)).collect(Collectors.toList());
        List<StayImage> stayImages = new ArrayList<>();
        for (String mediaLink : mediaLinks) {
            stayImages.add(new StayImage(mediaLink, stay));
        }
        // store stay in database
        stay.setImages(stayImages);
        stayRepository.save(stay);
        // store (id, geoPoint) into elastic search
        Location location = geoCodingService.getLatLng(stay.getId(), stay.getAddress());
        locationRepository.save(location);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }

        // If the to-be-deleted stay still have reservation from current time
        // we cannot delete the stay right now
        List<Reservation> reservations = reservationRepository.findByStayAndCheckoutDateAfter(stay, LocalDate.now());
        if (reservations != null && reservations.size() > 0) {
            throw new StayDeleteException("Cannot delete stay with active reservation");
        }


        // delete all this stay related data in stayReservedDates
        List<StayReservedDate> stayReservedDates = stayReservationDateRepository.findByStay(stay);

        for(StayReservedDate date : stayReservedDates) {
            stayReservationDateRepository.deleteById(date.getId());
        }

        stayRepository.deleteById(stayId);

    }
}


