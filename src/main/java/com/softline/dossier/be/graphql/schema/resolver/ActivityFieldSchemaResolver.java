package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.ActivityField;
import com.softline.dossier.be.graphql.types.input.ActivityFieldInput;
import com.softline.dossier.be.repository.ActivityFieldRepository;
import com.softline.dossier.be.service.ActivityFieldService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class ActivityFieldSchemaResolver extends SchemaResolverBase<ActivityField, ActivityFieldInput, ActivityFieldRepository, ActivityFieldService> {


    public ActivityField createActivityFieldI(ActivityFieldInput activityFieldInput) throws IOException, ClientReadableException {
        return create(activityFieldInput);
    }

    public ActivityField updateActivityField(ActivityFieldInput activityFieldInput) throws ClientReadableException {
        return update(activityFieldInput);
    }

    public boolean deleteActivityField(Long id) throws ClientReadableException {
        return delete(id);
    }

    protected List<ActivityField> getAllActivityField() {
        return getAll();
    }

    protected ActivityField getActivityField(Long id) {
        return get(id);
    }

    public List<ActivityField> getAllActivityFieldByActivityId(Long activityId) {
        return getService().getAllActivityFieldByActivityId(activityId);
    }
}
