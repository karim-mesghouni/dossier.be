package com.softline.dossier.be.service.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.domain.ActivityField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityFieldResolver implements GraphQLResolver<ActivityField> {

    public String getGroupName(ActivityField activityField) {
        if (activityField.getGroup() != null) {
            return activityField.getGroup().getName();
        }
        return null;
    }
}
