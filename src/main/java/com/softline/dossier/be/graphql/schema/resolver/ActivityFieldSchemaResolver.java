package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.domain.ActivityField;
import com.softline.dossier.be.graphql.types.input.ActivityFieldInput;
import com.softline.dossier.be.graphql.types.input.ActivityInput;
import com.softline.dossier.be.repository.ActivityFieldRepository;
import com.softline.dossier.be.repository.ActivityRepository;
import com.softline.dossier.be.service.ActivityFieldService;
import com.softline.dossier.be.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActivityFieldSchemaResolver extends SchemaResolverBase<ActivityField, ActivityFieldInput, ActivityFieldRepository, ActivityFieldService> {


    public ActivityField createActivityFieldI(ActivityFieldInput activityFieldInput){
        return create(activityFieldInput);
    }
    public ActivityField updateActivityField(ActivityFieldInput activityFieldInput){
        return update(activityFieldInput);
    }
    public boolean deleteActivityField(Long id){
        return delete(id);
    }
    protected List<ActivityField> getAllActivityField(){
        return getAll();
    }
    protected ActivityField getActivityField(Long id){
        return get(id);
    }
    public  List<ActivityField> getAllActivityFieldByActivityId(Long activityId){
    return     getService().getAllActivityFieldByActivityId(activityId);
    }
}
