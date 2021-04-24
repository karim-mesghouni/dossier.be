package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.graphql.types.input.ActivityInput;
import com.softline.dossier.be.repository.ActivityRepository;
import com.softline.dossier.be.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class ActivitySchemaResolver extends SchemaResolverBase<Activity, ActivityInput, ActivityRepository,ActivityService> {

    public Activity createActivity(ActivityInput Activity) throws IOException {
        return create(Activity);
    }
    public Activity updateActivity(ActivityInput Activity){
        return update(Activity);
    }
    public boolean deleteActivity(Long id){
        return delete(id);
    }
    protected List<Activity> getAllActivity(){
        return getAll();
    }
    protected Activity getActivity(Long id){
        return get(id);
    }
}
