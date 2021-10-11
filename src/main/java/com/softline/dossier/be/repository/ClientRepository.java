package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>
{
    List<Client> findAllWithContactsByNameContaining(String search);

    Client findWithFilesById(long id);
}
