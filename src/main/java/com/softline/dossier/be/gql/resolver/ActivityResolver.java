package com.softline.dossier.be.gql.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.domain.ActivityField;
import com.softline.dossier.be.gql.GQLExpetion;
import com.softline.dossier.be.repository.ActivityFieldRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.collection.internal.PersistentBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ActivityResolver implements GraphQLResolver<Activity> {
    @Autowired
    ActivityFieldRepository activityFieldRepository;


}
