package com.softline.dossier.be.service;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.ActivityField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class ActivityFieldService {


    public List<ActivityField> getAll() {
        return Database.findAll(ActivityField.class);
    }

    public boolean delete(long id) {
        return Database.remove(ActivityField.class, id);
    }

    public ActivityField getById(long id) {
        return Database.findOrThrow(ActivityField.class, id);
    }

    public List<ActivityField> getAllActivityFieldByActivityId(Long activityId) {
        return Database.query("SELECT f from ActivityField f where f.activity.id = :activityId", ActivityField.class)
                .setParameter("activityId", activityId)
                .getResultList();
    }

}
