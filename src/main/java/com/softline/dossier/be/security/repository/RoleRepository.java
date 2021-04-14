package com.softline.dossier.be.security.repository;

import com.softline.dossier.be.security.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

}
