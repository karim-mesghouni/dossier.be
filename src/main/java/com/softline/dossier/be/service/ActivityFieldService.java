package com.softline.dossier.be.service;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.ActivityField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityFieldService {
    public List<ActivityField> getAll() {
        return Database.findAll(ActivityField.class);
    }

    public ActivityField getById(Long id) {
        return Database.findOrThrow(ActivityField.class, id);
    }

    public List<ActivityField> getAllActivityFieldByActivityId(Long activityId) {
        return Database.query("SELECT f from ActivityField f where f.activity.id = :activityId", ActivityField.class)
                .setParameter("activityId", activityId)
                .getResultList();
    }

    public boolean delete(Long id) {
        Database.removeNow(ActivityField.class, id);
        return true;
    }


}
