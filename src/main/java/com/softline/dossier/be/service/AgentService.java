package com.softline.dossier.be.service;

import com.softline.dossier.be.graphql.types.input.AgentInput;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.casl.CaslRawRule;
import com.softline.dossier.be.security.repository.AgentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class AgentService extends IServiceBase<Agent, AgentInput, AgentRepository> {

    @Override
    public List<Agent> getAll() {
        return repository.findAll();
    }

    @Override
    public Agent create(AgentInput input) {
        return null;
    }

    @Override
    public Agent update(AgentInput input) {
        return null;
    }

    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Agent getById(long id) {
        return repository.findById(id).orElseThrow();
    }

    public Agent getCurrentAgent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var agent = (Agent) authentication.getPrincipal();
        if (agent != null) {
            Agent current = getRepository().findByUsername(agent.getUsername());
            // map authorities to caslRules
            List<CaslRawRule> rules = authentication.getAuthorities()
                    .stream()
                    .filter(e -> !e.getAuthority().startsWith("ROLE_"))
                    .map(authority -> {
                                var parts = authority.getAuthority().split("_");
                                return new CaslRawRule(parts[0].toLowerCase(), StringUtils.capitalize(parts[1].toLowerCase()));
                            }
                    ).collect(Collectors.toList());
            current.setCaslRules(rules);
            return current;
        }
        return null;
    }
}