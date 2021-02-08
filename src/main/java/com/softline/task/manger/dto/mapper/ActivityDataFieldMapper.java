package com.softline.task.manger.dto.mapper;

import com.softline.task.manger.domain.Activity;
import com.softline.task.manger.domain.ActivityDataField;
import com.softline.task.manger.domain.ActivityField;
import com.softline.task.manger.domain.FileActivity;
import com.softline.task.manger.dto.ActivityDataFieldDto;
import com.softline.task.manger.dto.ActivityFieldDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper(componentModel = "spring")
public interface ActivityDataFieldMapper {
    ActivityDataFieldMapper INSTANCE = Mappers.getMapper(ActivityDataFieldMapper.class);

    @Mapping(source = "fileActivity.id" ,target = "fileActivityId" ,nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ActivityDataFieldDto toActivityDataFieldDto(ActivityDataField activityDataField);

    List<ActivityDataFieldDto> toActivityDataFieldDtos(List<ActivityDataField> activityDataFields);

    @Mapping(source = "fileActivityId",target = "fileActivity",qualifiedByName = "fileActivityIdToFileActivity",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ActivityDataField toActivityDataField(ActivityDataFieldDto activityDataFieldDto);
    List<ActivityDataField> toActivityDataFields(List<ActivityDataFieldDto> activityDataFieldDtos);

    @Named("fileActivityIdToFileActivity")
    static FileActivity fileActivityIdToFileActivity(long id){
        return  FileActivity.builder().id(id).build();
    }
}
