package com.softline.task.manger.controller;

import com.softline.task.manger.dto.ActivityDto;
import com.softline.task.manger.dto.FileActivityDto;
import com.softline.task.manger.dto.mapper.ActivityMapper;
import com.softline.task.manger.dto.mapper.FileActivityMapper;
import com.softline.task.manger.repository.ActivityRepository;
import com.softline.task.manger.repository.FileActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fileactivity")
@CrossOrigin(origins = "*")
public class FileActivityController {

    @Autowired
    FileActivityRepository fileActivityRepository;
    @Autowired
    FileActivityMapper fileActivityMapper ;

    @PostMapping("/v1/save")
    public ResponseEntity<FileActivityDto> save(@RequestBody FileActivityDto activityDto){
        var fileActivity = fileActivityMapper.toFileActivity(activityDto);
        fileActivity.getDataFields().forEach((contact)->contact.setFileActivity(fileActivity));
        var savedFileActivity = fileActivityRepository.save(fileActivity);
        return ResponseEntity.ok().body(fileActivityMapper.toFileActivityDto(savedFileActivity));
    }

    @PutMapping("/v1/update")
    public ResponseEntity<FileActivityDto> update(@RequestBody FileActivityDto activityDto){
        var existingActivity = fileActivityRepository.findById(activityDto.getId());
        if(existingActivity.isPresent()){
            var activity = fileActivityMapper.toFileActivity(activityDto);
            activity.getDataFields().forEach((contact)->contact.setFileActivity(activity));
            fileActivityRepository.save(activity);
            return ResponseEntity.ok().body(fileActivityMapper.toFileActivityDto(activity));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @GetMapping("/v1/getall")
    public ResponseEntity<List<FileActivityDto>> getAll() {
        try {

            var activities = fileActivityRepository.findAll();
            if(!activities.isEmpty()){
                List<FileActivityDto> fileActivityDtos = fileActivityMapper.toFileActivityDtos(activities);
                return ResponseEntity.status(HttpStatus.OK).body(fileActivityDtos);
            }
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        catch (Exception exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
