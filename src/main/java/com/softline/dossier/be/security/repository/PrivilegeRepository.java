package com.softline.dossier.be.security.repository;

import com.softline.dossier.be.security.domain.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    @Query("SELECT privilege FROM Privilege privilege WHERE privilege.name = ?1")
    Privilege findByName(String name);


}
