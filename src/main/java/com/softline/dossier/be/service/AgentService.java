package com.softline.dossier.be.service;

import com.softline.dossier.be.Application;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.database.QueryFilter;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.AgentEvent;
import com.softline.dossier.be.security.domain.Agent;
import graphql.GraphQLException;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.softline.dossier.be.Tools.Functions.*;
import static com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator.DenyOrProceed;

@Transactional
@Service
public class AgentService {

    public List<Agent> getAll() {
        return Database.findAll(Agent.class);
    }


    public boolean changePassword(Agent input, String oldPassword) {
        return Database.findOrThrow(input, "CHANGE_PASSWORD", agent -> {
            throwIfEmpty(Application.getBean(PasswordEncoder.class).matches(oldPassword, agent.getPassword()), () -> new GraphQLException("le mot de passe ne correspond pas"));
            throwIfEmpty(input.getPassword(), () -> new GraphQLException("le mot de passe ne doit pas être vide"));
            agent.setPassword(Application.getBean(PasswordEncoder.class).encode(input.getPassword()));
            Database.flush();
            return true;
        });
    }

    public Agent create(Agent agent) {
        DenyOrProceed("CREATE_AGENT", agent);
        throwIfSuppliedEmpty(() -> agent.getRole().getId(), () -> new GraphQLException("veuillez spécifier une fonction"));
        agent.setRole(Database.findOrThrow(agent.getRole()));
        if (!Database.findOrThrow(agent.getRole()).isAdmin()) {
            throwIfSuppliedEmpty(() -> agent.getActivity().getId(), () -> new GraphQLException("veuillez spécifier une activité"));
            agent.setActivity(Database.findOrThrow(agent.getActivity()));
        }

        agent.setEnabled(true);
        agent.setPassword(Application.getBean(PasswordEncoder.class).encode(agent.getPassword()));
        Database.persist(agent);
        Database.flush();
        new AgentEvent(EntityEvent.Type.ADDED, agent).fireToAll();
        return agent;
    }

    public Agent update(Agent input) {
        return Database.findOrThrow(input, "UPDATE_AGENT", agent -> {
            safeRun(() -> agent.setUsername(throwIfEmpty(input.getUsername())));
            safeRun(() -> agent.setName(throwIfEmpty(input.getName())));
            safeRun(() -> agent.setActivity(Database.findOrThrow(input.getActivity())));
            safeRun(() -> agent.setJob(Database.findOrThrow(input.getJob())));
            safeRun(() -> agent.setRole(Database.findOrThrow(input.getRole())));
            safeRunIf(() -> input.getPassword().length() > 0,
                    () -> agent.setPassword(Application.getBean(PasswordEncoder.class).encode(input.getPassword())));
            Database.flush();
            new AgentEvent(EntityEvent.Type.UPDATED, agent).fireToAll();
            return agent;
        });
    }

    public boolean delete(long id) {
        return Database.findOrThrow(Agent.class, id, "DELETE_AGENT", agent -> {
            Database.remove(agent);
            new AgentEvent(EntityEvent.Type.DELETED, agent).fireToAll();
            return true;
        });
    }

    public Agent getById(long id) {
        return Database.findOrThrow(Agent.class, id);
    }

    public Agent getCurrentAgent() {
        return Agent.thisDBAgent();
    }


    public List<Agent> findBySearch(@Nullable String search) {
        QueryFilter<Agent> filter;
        if (search == null || search.isBlank())
            filter = (cq, cb, r) -> cq.orderBy(cb.desc(r.get("createdDate")));
        else
            filter = (cq, cb, r) -> cq.where(cb.or(cb.like(r.get("username"), "%" + search + "%"), cb.like(r.get("name")/**/, "%" + search + "%"))).orderBy(cb.desc(r.get("createdDate")));
        return Database.findAll(Agent.class, filter);
    }
}