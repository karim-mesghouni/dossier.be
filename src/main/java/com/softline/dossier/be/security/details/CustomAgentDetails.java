package com.softline.dossier.be.security.details;

import com.softline.dossier.be.security.domain.Agent;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class CustomAgentDetails implements UserDetails
{

    private final Agent agent;
    Collection<GrantedAuthority> grantedAuthorities;

    public CustomAgentDetails(Agent agent, Collection<GrantedAuthority> grantedAuthorities)
    {

        this.grantedAuthorities = grantedAuthorities;
        this.agent = agent;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return grantedAuthorities;
    }

    @Override
    public String getPassword()
    {
        return agent.getPassword();
    }

    @Override
    public String getUsername()
    {
        return agent.getUsername();
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return agent.isEnabled();
    }
}
