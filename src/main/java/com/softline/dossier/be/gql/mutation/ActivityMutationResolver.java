package com.softline.dossier.be.gql.mutation;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.gql.type.ActivityInput;
import com.softline.dossier.be.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityMutationResolver  implements GraphQLMutationResolver {
    @Autowired
    ActivityService activityService;
    public Activity createActivity(ActivityInput activityInput){
       return activityService.createActivity(activityInput);
    }
    public Activity updateActivity(ActivityInput activityInput){
        return activityService.updateActivity(activityInput);
    }
}
