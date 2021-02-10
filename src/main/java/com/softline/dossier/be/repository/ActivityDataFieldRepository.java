package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.ActivityDataField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityDataFieldRepository extends JpaRepository<ActivityDataField,Long> {
}
