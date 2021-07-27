package com.softline.dossier.be.security.filters;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softline.dossier.be.security.details.CustomAgentDetails;
import com.softline.dossier.be.security.domain.Agent;
import graphql.GraphQLException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.softline.dossier.be.security.filters.constants.SecurityConstants.*;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            Agent agent = new ObjectMapper()
                    .readValue(req.getInputStream(), Agent.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            agent.getUsername(),
                            agent.getPassword(),
                            Arrays.asList())
            );
        } catch (IOException e) {
            throw new GraphQLException(e);
        }

    }
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {
        String token = createToken(auth.getName(), auth);
        res.setHeader("Access-Control-Expose-Headers", HEADER_STRING);
        res.addHeader(HEADER_STRING,  token);
    }
    private String createToken(String name, Authentication auth) {
        List<String> grantedAuthorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : auth.getAuthorities()) {
            grantedAuthorities.add(grantedAuthority.getAuthority());
        }
        return JWT.create()
                .withSubject(name)
                .withClaim("grantedAuthorities", grantedAuthorities)
                .withClaim("id", ((CustomAgentDetails)auth.getPrincipal()).getAgent().getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
    }
}
