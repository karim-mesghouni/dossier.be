package com.softline.dossier.be.service.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.repository.ActivityFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityResolver implements GraphQLResolver<Activity> {
    @Autowired
    ActivityFieldRepository activityFieldRepository;


}
