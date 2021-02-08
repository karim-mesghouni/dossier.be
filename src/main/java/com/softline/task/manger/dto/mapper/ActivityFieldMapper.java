package com.softline.task.manger.dto.mapper;

import com.softline.task.manger.domain.Activity;
import com.softline.task.manger.domain.ActivityField;
import com.softline.task.manger.dto.ActivityFieldDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityFieldMapper {

    ActivityFieldMapper INSTANCE = Mappers.getMapper(ActivityFieldMapper.class);

    @Mapping(source = "activity.id" ,target = "activityId" ,nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ActivityFieldDto toActivityFieldDto(ActivityField activityField);

    List<ActivityFieldDto> toActivityFieldDtos(List<ActivityField> activityFields);

    @Mapping(source = "activityId",target = "activity",qualifiedByName = "activityIdToActivity",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ActivityField toActivityField(ActivityFieldDto activityFieldDto);
    List<ActivityField> toActivityFields(List<ActivityFieldDto> activityFieldDtos);

    @Named("activityIdToActivity")
    static Activity activityIdToActivity(long id){
        return  Activity.builder().id(id).build();
    }

}
