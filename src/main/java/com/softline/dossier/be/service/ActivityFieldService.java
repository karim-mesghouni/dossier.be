package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.domain.ActivityField;
import com.softline.dossier.be.graphql.types.input.ActivityFieldInput;
import com.softline.dossier.be.graphql.types.input.ActivityInput;
import com.softline.dossier.be.repository.ActivityFieldRepository;
import com.softline.dossier.be.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class ActivityFieldService extends IServiceBase<ActivityField, ActivityFieldInput, ActivityFieldRepository> {


    @Override
    public List<ActivityField> getAll() {
        return repository.findAll();
    }

    @Override
    public ActivityField create(ActivityFieldInput entityInput) {
  return  null;
    }

    @Override
    public ActivityField update(ActivityFieldInput entityInput) {
        return null;
    }

    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public ActivityField getById(long id) {
        return repository.findById(id).orElseThrow();
    }
    public  List<ActivityField> getAllActivityFieldByActivityId(Long activityId){
       return   getRepository().getActivityFieldByActivity_Id(activityId);
    }

}
