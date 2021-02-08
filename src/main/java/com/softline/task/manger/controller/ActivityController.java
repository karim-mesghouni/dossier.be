package com.softline.task.manger.controller;

import com.softline.task.manger.dto.ActivityDto;
import com.softline.task.manger.dto.mapper.ActivityMapper;
import com.softline.task.manger.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*")
public class ActivityController {
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ActivityMapper activityMapper;

    @PostMapping("/v1/save")
    public ResponseEntity<ActivityDto> save(@RequestBody ActivityDto activityDto){
            var activity = activityMapper.toActivity(activityDto);
            activity.getFields().forEach((contact)->contact.setActivity(activity));
            var savedActivity = activityRepository.save(activity);
            return ResponseEntity.ok().body(activityMapper.toActivityDto(savedActivity));
    }

    @PutMapping("/v1/update")
    public ResponseEntity<ActivityDto> update(@RequestBody ActivityDto activityDto){
        var existingActivity = activityRepository.findById(activityDto.getId());
        if(existingActivity.isPresent()){
            var activity = activityMapper.toActivity(activityDto);
            activity.getFields().forEach((contact)->contact.setActivity(activity));
            activityRepository.save(activity);
            return ResponseEntity.ok().body(activityMapper.toActivityDto(activity));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @GetMapping("/v1/getall")
    public ResponseEntity<List<ActivityDto>> getAll() {
        try {

            var activities = activityRepository.findAll();
            if(!activities.isEmpty()){
                List<ActivityDto> activityDtos = activityMapper.toActivityDtos(activities);
                return ResponseEntity.status(HttpStatus.OK).body(activityDtos);
            }
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        catch (Exception exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
