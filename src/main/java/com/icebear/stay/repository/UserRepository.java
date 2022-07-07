package com.icebear.stay.repository;

import com.icebear.stay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// JPARepository<model we want, type of primary key>
// By extending the JpaRepository, Spring can help provide some default implementations of common database operations.
public interface UserRepository extends JpaRepository<User, String> {
    // since the interface extends JPARepository,
    // the implementation of this interface is automatically created by Spring-data
    // And Spring will generate the concrete object since @Repository can be autowired in anywhere
}
