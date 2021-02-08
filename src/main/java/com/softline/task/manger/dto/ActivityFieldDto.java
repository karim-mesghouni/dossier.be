package com.softline.task.manger.dto;


import com.softline.task.manger.domain.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityFieldDto {

    long id;
    String fieldName;

    FieldType  fieldType;

    long activityId;
}
