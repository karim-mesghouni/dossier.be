package com.softline.task.manger.dto.mapper;

import com.softline.task.manger.domain.Activity;
import com.softline.task.manger.domain.ActivityDataField;
import com.softline.task.manger.domain.FileActivity;
import com.softline.task.manger.dto.ActivityDataFieldDto;
import com.softline.task.manger.dto.FileActivityDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper(componentModel = "spring")
public interface FileActivityMapper {
    FileActivityMapper INSTANCE = Mappers.getMapper(FileActivityMapper.class);

    @Mapping(source = "activity.id",target = "activityId",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "activity.name",target = "activityName",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "dataFields",target = "dataFields",qualifiedByName = "dataFieldsToDataFieldsDtos",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    FileActivityDto toFileActivityDto(FileActivity fileActivity);
    List<FileActivityDto> toFileActivityDtos(List<FileActivity>  fileActivities);

    @Mapping(source = "activityId",target = "activity",qualifiedByName = "activityIdToActivity",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "dataFields",target = "dataFields",qualifiedByName = "dataFieldsDtosToDataFields",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    FileActivity toFileActivity(FileActivityDto  fileActivityDto);
    List<FileActivity>  toFileActivities(List<FileActivityDto> fileActivityDtos);

    @Named("dataFieldsToDataFieldsDtos")
    static ActivityDataFieldDto toActivityDataFieldDto(ActivityDataField activityDataField){
        return   Mappers.getMapper(ActivityDataFieldMapper.class).toActivityDataFieldDto(activityDataField);
    }
    @Named("dataFieldsDtosToDataFields")
    static ActivityDataField dataFieldsDtosToDataFields(ActivityDataFieldDto activityDataFieldDto){
        return   Mappers.getMapper(ActivityDataFieldMapper.class).toActivityDataField(activityDataFieldDto);
    }
    @Named("activityIdToActivity")
    static Activity activityIdToActivity(long id){
        return  Activity.builder().id(id).build();
    }
}
