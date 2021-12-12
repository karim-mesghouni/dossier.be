package com.softline.dossier.be.security.service;

import com.softline.dossier.be.security.details.CustomAgentDetails;
import com.softline.dossier.be.security.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentDetailsService implements UserDetailsService {
    private final AgentRepository agentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var agent = agentRepository.findByUsername(username);
        if (agent == null) {
            throw new UsernameNotFoundException("Could not find any user with username: " + username);
        }
        if (agent.getRole() == null) {
            throw new NullPointerException("Agent has no role");
        }
        return new CustomAgentDetails(agent, List.of(new SimpleGrantedAuthority(agent.getRole().getName())));
    }
}
