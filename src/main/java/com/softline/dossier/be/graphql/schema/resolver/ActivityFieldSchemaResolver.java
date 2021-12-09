package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.domain.ActivityField;
import com.softline.dossier.be.service.ActivityFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ActivityFieldSchemaResolver implements GraphQLQueryResolver {
    private final ActivityFieldService service;

    protected List<ActivityField> getAllActivityField() {
        return service.getAll();
    }

    protected ActivityField getActivityField(Long id) {
        return service.getById(id);
    }

    public List<ActivityField> getAllActivityFieldByActivityId(Long id) {
        return service.getAllActivityFieldByActivityId(id);
    }
}
