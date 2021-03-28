package com.softline.dossier.be.service.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.domain.ActivityField;

public class ActivityFieldResolver implements GraphQLResolver<ActivityField> {

 public  String getGroupName(ActivityField activityField){
        return  activityField.getGroup().getName();
    }
}
