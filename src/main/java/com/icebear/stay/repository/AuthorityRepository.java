package com.icebear.stay.repository;

import com.icebear.stay.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// <model we want, type of primary key>
// == <Authority, type of username>
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
    Authority findAuthorityByUsername(String username);
}

