package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.domain.ActivityField;
import com.softline.dossier.be.gql.type.ActivityInput;
import com.softline.dossier.be.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional

public class ActivityService {

    @Autowired
    ActivityRepository activityRepository;
    public  List<Activity> getAll(){
       return  activityRepository.findAll();
   }
    public Activity createActivity(ActivityInput activityInput) {
             Activity activity= Activity.builder()
                .name(activityInput.getName())
                .description(activityInput.getDescription())
                .fields(activityInput.getFields().stream()
                        .map(x-> ActivityField.builder()
                                .fieldName(x.getFieldName())
                                .fieldType(x.getFieldType())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        activity.getFields().forEach(x->x.setActivity(activity));
        return  activityRepository.save(activity);
    }
    public Activity updateActivity(ActivityInput activityInput) {
         Activity activity =  activityRepository.getOne(activityInput.getId());
         activity.setName(activityInput.getName());
        return  activity;
    }
    public void deleteActivity(long id) {
         activityRepository.deleteById(id);
    }
    public Activity  getById(long id){
       return    activityRepository.findById(id).orElseThrow();
    }
}
