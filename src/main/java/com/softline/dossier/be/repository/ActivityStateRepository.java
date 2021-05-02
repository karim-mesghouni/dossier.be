package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.ActivityState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityStateRepository extends JpaRepository<ActivityState,Long> {
    ActivityState findFirstByInitialIsTrueAndActivity_Id(Long activityId);
}
