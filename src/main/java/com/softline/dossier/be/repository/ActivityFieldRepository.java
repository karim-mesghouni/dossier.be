package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.ActivityField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ActivityFieldRepository extends JpaRepository<ActivityField, Long>
{


    List<ActivityField> getActivityFieldByActivity_Id(long activityId);
}
