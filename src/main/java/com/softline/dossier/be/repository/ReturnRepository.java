package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long>
{
}
