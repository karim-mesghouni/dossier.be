package com.softline.dossier.be.gql.query;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class ActivityQuery implements GraphQLQueryResolver {
      @Autowired
     ActivityService activityService;
     public List<Activity> getAllActivity(){
            return  activityService.getAll();
     }

}
