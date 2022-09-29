package com.icebear.stay.service;


import com.google.maps.model.GeocodingResult;
import com.icebear.stay.model.Stay;
import com.icebear.stay.repository.LocationRepository;
import com.icebear.stay.repository.StayRepository;
import com.icebear.stay.repository.StayReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
// search by distance and return list of valid stays
public class SearchService {
    private StayRepository stayRepository;
    private StayReservationDateRepository stayReservationDateRepository;
    private LocationRepository locationRepository;
    private GeoCodingService geoCodingService;


    @Autowired
    public SearchService(StayRepository stayRepository,
                         StayReservationDateRepository stayReservationDateRepository,
                         LocationRepository locationRepository,
                         GeoCodingService geoCodingService
                         ) {
        this.stayRepository = stayRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
        this.locationRepository = locationRepository;
        this.geoCodingService = geoCodingService;
    }


    public List<Stay> search(int guestNumber, LocalDate checkinDate, LocalDate checkoutDate,
                             String address, String distance) {
        GeocodingResult result = geoCodingService.getLatLng(address);
        double lat = result.geometry.location.lat;
        double lon = result.geometry.location.lng;

        List<Long> stayIds = locationRepository.searchByDistance(lat, lon, distance);
        // if no stay is searched out, return the empty list
        if (stayIds == null || stayIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> reservedStayIds = stayReservationDateRepository.findByIdInAndDateBetween(
                stayIds, checkinDate, checkoutDate.minusDays(1));

        List<Long> filteredStayIds = new ArrayList<>();

        // filter to find out all unreserved stay
        for (Long stayId : stayIds) {
            if (!reservedStayIds.contains(stayId)) {
                filteredStayIds.add(stayId);
            }
        }
        return stayRepository.findByIdInAndGuestNumberGreaterThanEqual(filteredStayIds, guestNumber);
    }
}

