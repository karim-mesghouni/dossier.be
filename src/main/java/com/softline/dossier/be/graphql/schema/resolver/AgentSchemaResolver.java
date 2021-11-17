package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.Tools.Database;
import com.softline.dossier.be.domain.Job;
import com.softline.dossier.be.graphql.types.input.AgentInput;
import com.softline.dossier.be.repository.ActivityRepository;
import com.softline.dossier.be.repository.JobRepository;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Role;
import com.softline.dossier.be.security.repository.AgentRepository;
import com.softline.dossier.be.security.repository.RoleRepository;
import com.softline.dossier.be.service.AgentService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;

@Component
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class AgentSchemaResolver extends SchemaResolverBase<Agent, AgentInput, AgentRepository, AgentService> {
    final PasswordEncoder passwordEncoder;
    final RoleRepository roleRepository;
    final JobRepository jobRepository;
    final ActivityRepository activityRepository;
    final ModelMapper modelMapper;
    final EntityManager entityManager;

    public List<Agent> getAllAgent() {
        return getAll();
    }

    public Agent getAgent(Long id) {
        return get(id);
    }

    public Agent getCurrentAgent() {
        return service.getCurrentAgent();
    }

    public List<Role> allRoles() {
        return Database.findAll(Role.class);
    }

    public List<Job> allJobs() {
        return Database.findAll(Job.class);
    }

    public boolean deleteAgent(Long id) throws ClientReadableException {
        return delete(id);
    }

    public Agent updateAgent(AgentInput input) throws ClientReadableException {
        return update(input);
    }

    public boolean changePassword(AgentInput input, String oldPassword) throws ClientReadableException {
        return service.changePassword(input, oldPassword);
    }

    public Agent createAgent(AgentInput input) throws ClientReadableException, IOException {
        return create(input);
    }

    public boolean deleteAgent(long id) throws ClientReadableException {
        return delete(id);
    }

    public List<Agent> findAgentBySearch(@Nullable String search) {
        return service.findBySearch(search);
    }
}
