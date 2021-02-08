package com.softline.task.manger.dto.mapper;

import com.softline.task.manger.domain.Activity;
import com.softline.task.manger.domain.ActivityField;
import com.softline.task.manger.dto.ActivityDto;
import com.softline.task.manger.dto.ActivityFieldDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",uses = {ActivityFieldMapper.class})
public interface ActivityMapper {
    ActivityMapper INSTANCE = Mappers.getMapper(ActivityMapper.class);
    @Mapping(source = "fields" ,target = "fields" ,qualifiedByName = "fieldsToFieldDtos",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ActivityDto toActivityDto(Activity activity);
    List<ActivityDto> toActivityDtos(List<Activity> activities);

    @Mapping(source = "fields" ,target = "fields" ,qualifiedByName = "fieldDtosTOFields",nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Activity toActivity(ActivityDto activityDto);
    List<Activity> toActivities(List<ActivityDto> activityDtos);

    @Named("fieldsToFieldDtos")
    static ActivityFieldDto  fieldsToFieldDtos(ActivityField  field){
        return Mappers.getMapper(ActivityFieldMapper.class).toActivityFieldDto(field);
    }
    @Named("fieldDtosTOFields")
    static ActivityField  fieldsToFieldDtos(ActivityFieldDto  field){

        return ActivityField.builder().id(field.getId()).build();
    }

}
