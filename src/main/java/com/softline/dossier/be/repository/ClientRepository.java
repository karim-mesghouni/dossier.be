package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository   extends JpaRepository<Client,Long> {
}
