package com.softline.task.manger.dto;

import com.softline.task.manger.domain.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityDataFieldDto {


    long id;
    String fieldName;


    FieldType fieldType;
    String data;

    long fileActivityId;
}
