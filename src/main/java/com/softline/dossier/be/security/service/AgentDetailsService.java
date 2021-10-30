package com.softline.dossier.be.security.service;

import com.softline.dossier.be.security.details.CustomAgentDetails;
import com.softline.dossier.be.security.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class AgentDetailsService implements UserDetailsService {
    @Autowired
    private AgentRepository agentRepository;
    private Collection<GrantedAuthority> grantedAuthorities;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var agent = agentRepository.findByUsername(username);
        if (agent == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomAgentDetails(agent, List.of(new SimpleGrantedAuthority(agent.getRole().getName())));
    }
}
