package com.softline.task.manger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileActivityDto {


    long id;

    long  activityId;
    String activityName;
    List<ActivityDataFieldDto> dataFields;
}
