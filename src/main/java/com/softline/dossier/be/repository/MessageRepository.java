package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Message;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByAgent_Id(Long agentId, Sort sort);
}
