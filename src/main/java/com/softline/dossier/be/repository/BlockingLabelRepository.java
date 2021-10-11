package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.BlockingLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface BlockingLabelRepository extends JpaRepository<BlockingLabel, Long>
{
}
