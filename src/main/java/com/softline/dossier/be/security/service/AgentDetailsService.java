package com.softline.dossier.be.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softline.dossier.be.security.details.CustomAgentDetails;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.repository.AgentRepository;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AgentDetailsService implements UserDetailsService {
    @Autowired
    private AgentRepository agentRepository;
    private  Collection<GrantedAuthority> grantedAuthorities;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var agent = agentRepository.findFirstByUsername(username);
        if (agent == null) {
            throw new UsernameNotFoundException(username);
        }


        grantedAuthorities = new ArrayList<>();
                if (agent.getRoles()!=null){
                    agent.getRoles().forEach(role -> {
                        grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
                        if (role.getPrivileges()!=null){
                            role.getPrivileges().forEach(privilege -> {
                                grantedAuthorities.add(new SimpleGrantedAuthority(privilege.getName()));
                            });
                        }
                    });
             }

        return new CustomAgentDetails(agent, grantedAuthorities);
    }
}
