package com.softline.dossier.be.service;

import com.softline.dossier.be.Application;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.AgentEvent;
import com.softline.dossier.be.security.domain.Agent;
import graphql.GraphQLException;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.softline.dossier.be.Tools.Functions.*;
import static com.softline.dossier.be.security.config.Gate.check;

@Service
public class AgentService {

    public List<Agent> getAll() {
        return Database.query("SELECT a FROM Agent a where a.deleted = false", Agent.class).getResultList();
    }

    public Agent getById(Long id) {
        return Database.findOrThrow(Agent.class, id);
    }

    public Agent getCurrentAgent() {
        return Agent.thisDBAgent();
    }


    public List<Agent> findBySearch(@Nullable String search) {
        return Database.em().createNamedQuery("Agent.findBySearch", Agent.class)
                .setParameter("search", search == null ? "" : search)
                .getResultList();
    }

    public boolean changePassword(Agent input, String oldPassword) {
        return Database.findOrThrow(input, "CHANGE_PASSWORD", agent -> {
            Database.startTransaction();
            throwIfEmpty(Application.getBean(PasswordEncoder.class).matches(oldPassword, agent.getPassword()), () -> new GraphQLException("le mot de passe ne correspond pas"));
            throwIfEmpty(input.getPassword(), () -> new GraphQLException("le mot de passe ne doit pas être vide"));
            agent.setPassword(Application.getBean(PasswordEncoder.class).encode(input.getPassword()));
            Database.commit();
            return true;
        });
    }

    public Agent create(Agent agent) {
        check("CREATE_AGENT", agent);
        throwIfEmpty(agent.getUsername(), () -> new GraphQLException("le nom d'utilisateur ne peut pas être vide"));
        throwIfEmpty(agent.getName(), () -> new GraphQLException("le nom ne peut pas être vide"));
        throwIfSuppliedEmpty(() -> agent.getRole().getId(), () -> new GraphQLException("veuillez spécifier une fonction"));
        agent.setRole(Database.findOrThrow(agent.getRole()));
        agent.setEnabled(true);
        agent.setPassword(Application.getBean(PasswordEncoder.class).encode(agent.getPassword()));
        Database.startTransaction();
        Database.persist(agent);
        Database.commit();
        new AgentEvent(EntityEvent.Type.ADDED, agent).fireToAll();
        return agent;
    }

    public Agent update(Agent input) {
        return Database.findOrThrow(input, "UPDATE_AGENT", agent -> {
            Database.startTransaction();
            agent.setUsername(throwIfEmpty(input.getUsername(), () -> new GraphQLException("le nom d'utilisateur ne peut pas être vide")));
            agent.setName(throwIfEmpty(input.getName(), () -> new GraphQLException("le nom ne peut pas être vide")));
            safeRun(() -> agent.setJob(Database.findOrThrow(input.getJob())));
            safeRun(() -> agent.setRole(Database.findOrThrow(input.getRole())));
            safeRunIf(() -> input.getPassword().length() > 0,
                    () -> agent.setPassword(Application.getBean(PasswordEncoder.class).encode(input.getPassword())));
            Database.commit();
            new AgentEvent(EntityEvent.Type.UPDATED, agent).fireToAll();
            return agent;
        });
    }

    public void delete(Long id) {
        Database.afterRemoving(Agent.class, id, "DELETE_AGENT",
                agent -> new AgentEvent(EntityEvent.Type.DELETED, agent).fireToAll());
    }


}