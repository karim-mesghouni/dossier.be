package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Job;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.AgentEvent;
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
import org.hibernate.Session;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.util.List;

import static com.softline.dossier.be.Tools.Functions.safeRun;
import static com.softline.dossier.be.Tools.Functions.throwIfEmpty;

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
        return roleRepository.findAll();
    }

    public List<Job> allJobs() {
        return jobRepository.findAll();
    }

    @PreAuthorize("hasPermission(null, 'DELETE_AGENT')")
    public boolean deleteAgent(Long id) throws ClientReadableException {
        return delete(id);
    }

    @PreAuthorize("hasPermission(null, 'UPDATE_AGENT')")
    public Agent updateAgent(AgentInput input) {
        Agent agent = entityManager.find(Agent.class, input.getId());
        safeRun(() -> agent.setUsername(throwIfEmpty(input.getUsername())));
        safeRun(() -> agent.setName(throwIfEmpty(input.getName())));
        safeRun(() -> agent.setActivity(activityRepository.findById(input.getActivity().getId()).orElseThrow()));
        safeRun(() -> agent.setJob(jobRepository.findById(input.getJob().getId()).orElseThrow()));
        safeRun(() -> agent.setRole(roleRepository.findById(input.getRole().getId()).orElseThrow()));
        safeRun(() -> input.getPassword().length() > 0,
                () -> agent.setPassword(passwordEncoder.encode(input.getPassword())));
        service.getRepository().save(agent);
        new AgentEvent(EntityEvent.Type.UPDATED, agent).fireToAll();
        return agent;
    }

    public boolean changePassword(AgentInput input, String oldPassword) throws ClientReadableException {
        Agent agent = entityManager.find(Agent.class, input.getId());
        throwIfEmpty(passwordEncoder.matches(oldPassword, agent.getPassword()), new ClientReadableException("le mot de passe ne correspond pas"));
        throwIfEmpty(input.getPassword(), new ClientReadableException("le mot de passe ne doit pas être vide"));
        agent.setPassword(passwordEncoder.encode(input.getPassword()));
        service.getRepository().save(agent);
        new AgentEvent(EntityEvent.Type.UPDATED, agent).fireToAll();
        return true;
    }

    @PreAuthorize("hasPermission(null, 'CREATE_AGENT')")
    public Agent createAgent(AgentInput input) {
        var agent = modelMapper.map(input, Agent.class);
        agent.setEnabled(true);
        agent.setPassword(passwordEncoder.encode(input.getPassword()));
        Long id = (Long) entityManager.unwrap(Session.class).save(agent);
        entityManager.clear();
        agent = entityManager.find(Agent.class, id);
        new AgentEvent(EntityEvent.Type.ADDED, agent).fireToAll();
        return agent;
    }

    @PreAuthorize("hasPermission(null, 'DELETE_AGENT')")
    public boolean deleteAgent(long id) {
        var agent = service.getRepository().findById(id).orElseThrow();
        service.getRepository().deleteById(id);
        new AgentEvent(EntityEvent.Type.DELETED, agent).fireToAll();
        return true;
    }

    public List<Agent> findAgentBySearch(@Nullable String search) {
        return service.findBySearch(search);
    }
}
