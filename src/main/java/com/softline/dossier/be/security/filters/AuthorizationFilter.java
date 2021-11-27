package com.softline.dossier.be.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Role;
import com.softline.dossier.be.security.policy.PolicyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.softline.dossier.be.security.filters.constants.SecurityConstants.*;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(PolicyMatcher.class);


    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        String header = req.getHeader(HEADER_STRING);
        try {
            if (header != null && header.startsWith(TOKEN_PREFIX)) {
                UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Throwable e) {
            logger.error("got invalid jwt token ");
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "invalid token");
        }
        chain.doFilter(req, res);
    }


    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.

            DecodedJWT decoded = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""));
            String role = decoded.getClaim("role").asString();

            Agent agent = new Agent();
            agent.setId(decoded.getClaim("id").asLong());
            agent.setUsername(decoded.getSubject());
            agent.setRole(Role.builder().type(Role.Type.valueOf(role)).build());
            agent.setActivity(Activity.builder().id(decoded.getClaim("activityId").as(Long.class)).build());
            return new UsernamePasswordAuthenticationToken(agent, null, List.of(new SimpleGrantedAuthority(role)));
        }
        return null;
    }
}