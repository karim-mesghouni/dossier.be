package com.softline.dossier.be.service;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.domain.Job;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.AgentEvent;
import com.softline.dossier.be.graphql.types.input.AgentInput;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Role;
import com.softline.dossier.be.security.repository.AgentRepository;
import graphql.GraphQLException;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.softline.dossier.be.Application.context;
import static com.softline.dossier.be.Tools.Functions.safeRun;
import static com.softline.dossier.be.Tools.Functions.throwIfEmpty;
import static com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator.throwIfCannot;

@Transactional
@Service
public class AgentService extends IServiceBase<Agent, AgentInput, AgentRepository> {

    @Override
    public List<Agent> getAll() {
        return repository.findAll();
    }


    public boolean changePassword(AgentInput input, String oldPassword) {
        return Database.findOrThrow(Agent.class, input, "CHANGE_PASSWORD", agent -> {
            throwIfEmpty(context().getBean(PasswordEncoder.class).matches(oldPassword, agent.getPassword()), () -> new GraphQLException("le mot de passe ne correspond pas"));
            throwIfEmpty(input.getPassword(), () -> new GraphQLException("le mot de passe ne doit pas être vide"));
            agent.setPassword(context().getBean(PasswordEncoder.class).encode(input.getPassword()));
            Database.flush();
            return true;
        });
    }

    @Override
    public Agent create(AgentInput input) {
        throwIfCannot("CREATE_AGENT", input);
        var agent = context().getBean(ModelMapper.class).map(input, Agent.class);
        agent.setEnabled(true);
        agent.setPassword(context().getBean(PasswordEncoder.class).encode(input.getPassword()));
        Database.persist(agent);
        Database.flush();
        new AgentEvent(EntityEvent.Type.ADDED, agent).fireToAll();
        return agent;
    }

    @Override
    public Agent update(AgentInput input) {
        return Database.findOrThrow(Agent.class, input, "UPDATE_AGENT", agent -> {
            safeRun(() -> agent.setUsername(throwIfEmpty(input.getUsername())));
            safeRun(() -> agent.setName(throwIfEmpty(input.getName())));
            safeRun(() -> agent.setActivity(Database.findOrThrow(Activity.class, input.getActivity())));
            safeRun(() -> agent.setJob(Database.findOrThrow(Job.class, input.getJob())));
            safeRun(() -> agent.setRole(Database.findOrThrow(Role.class, input.getRole())));
            safeRun(() -> input.getPassword().length() > 0,
                    () -> agent.setPassword(context().getBean(PasswordEncoder.class).encode(input.getPassword())));
            Database.flush();
            new AgentEvent(EntityEvent.Type.UPDATED, agent).fireToAll();
            return agent;
        });
    }

    @Override
    public boolean delete(long id) {
        return Database.findOrThrow(Agent.class, id, "DELETE_AGENT", agent -> {
            Database.remove(agent);
            Database.flush();
            new AgentEvent(EntityEvent.Type.DELETED, agent);
            return true;
        });
    }

    @Override
    public Agent getById(long id) {
        return Database.findOrThrow(Agent.class, id);
    }

    public Agent getCurrentAgent() {
        return Agent.thisDBAgent();
    }


    public List<Agent> findBySearch(String search) {
        return Database.em().createQuery("select a from Agent a where " +
                        ":search is null or a.username like CONCAT('%', :search, '%') " +
                        "or a.name like CONCAT('%', :search, '%') " +
                        "order by a.role.name, a.activity.name", Agent.class)
                .setParameter("search", search)
                .getResultList();
    }
}