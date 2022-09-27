package com.icebear.stay.service;

import com.icebear.stay.exception.ReservationCollisionException;
import com.icebear.stay.exception.ReservationNotFoundException;
import com.icebear.stay.model.*;
import com.icebear.stay.repository.ReservationRepository;
import com.icebear.stay.repository.StayRepository;
import com.icebear.stay.repository.StayReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;
    private StayReservationDateRepository stayReservationDateRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              StayReservationDateRepository stayReservationDateRepository
                              ) {
        this.reservationRepository = reservationRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
    }

    public List<Reservation> listByGuest(String username) {
        return reservationRepository.findByGuest(new User.Builder().setUsername(username).build());
    }

    public List<Reservation> listByStay(Long stayId) {
        return reservationRepository.findByStay(new Stay.Builder().setId(stayId).build());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void add(Reservation reservation) throws ReservationCollisionException {
        // find this stay between checkinDate and checkoutDate - 1
        // does not care about availability on checkoutDate, user will not use the stay that night
        // So no need to search for checkoutDate
        Set<Long> stayIds = stayReservationDateRepository.findByIdInAndDateBetween(
                Arrays.asList(reservation.getStay().getId()),
                reservation.getCheckinDate(),
                reservation.getCheckoutDate().minusDays(1));
        // means this stay has already been reserved
        if (!stayIds.isEmpty()) {
            throw new ReservationCollisionException("Duplicate reservation");
        }
        // process of adding reservation into database: stayReservationDate and reservation separately
        List<StayReservedDate> reservedDates = new ArrayList<>();
        // checkoutDate is available to others, it will not be added to database
        for (LocalDate date = reservation.getCheckinDate(); date.isBefore(reservation.getCheckoutDate()); date = date.plusDays(1)) {
            reservedDates.add(new StayReservedDate(new StayReservedDateKey(reservation.getStay().getId(), date), reservation.getStay()));
        }
        stayReservationDateRepository.saveAll(reservedDates);
        reservationRepository.save(reservation);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long reservationId, String username) {
        /*
        username used to avoid a user requests to delete a stay that is not reserved by her.
         */
        Reservation reservation = reservationRepository.findByIdAndGuest(reservationId, new User.Builder().setUsername(username).build());
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation is not available");
        }
        // delete reservation info in stayReservationDate database
        for (LocalDate date = reservation.getCheckinDate(); date.isBefore(reservation.getCheckoutDate()); date = date.plusDays(1)) {
            stayReservationDateRepository.deleteById(new StayReservedDateKey(reservation.getStay().getId(), date));
        }
        reservationRepository.deleteById(reservationId);
    }

}
