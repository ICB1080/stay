package com.icebear.stay.repository;


import com.icebear.stay.model.Stay;
import com.icebear.stay.model.StayReservedDate;
import com.icebear.stay.model.StayReservedDateKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface StayReservationDateRepository extends JpaRepository<StayReservedDate, StayReservedDateKey> {
    // srd is the abbreviations of stayReservationDate
    // ?1 first argument in parentheses, ?2 second argument in parentheses
    @Query(value = "SELECT srd.id.stay_id FROM StayReservedDate srd " +
            "WHERE srd.id.stay_id IN ?1 AND srd.id.date BETWEEN ?2 AND ?3 GROUP BY srd.id.stay_id")
    // find stayIds that has already been reserved between startDate and endDate
    Set<Long> findByIdInAndDateBetween(List<Long> stayIds, LocalDate startDate, LocalDate endDate);

    List<StayReservedDate> findByStay(Stay stay);
}

