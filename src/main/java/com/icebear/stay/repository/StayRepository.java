package com.icebear.stay.repository;

import com.icebear.stay.model.Stay;
import com.icebear.stay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {

//    https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
    // host, id are name of column in table Stay

    List<Stay> findByHost(User host);

    Stay findByIdAndHost(Long id, User host);


    // find all stays which can contain guest number >= guestNumber
    List<Stay> findByIdInAndGuestNumberGreaterThanEqual(List<Long> ids, int guestNumber);

}
