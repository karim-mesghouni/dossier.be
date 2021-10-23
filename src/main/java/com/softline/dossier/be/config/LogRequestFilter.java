package com.softline.dossier.be.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LogRequestFilter extends BasicAuthenticationFilter
{
    public LogRequestFilter(AuthenticationManager authenticationManager)
    {
        super(authenticationManager);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException
    {
        log.info("[{}] {} Authorization:{}", req.getMethod(), req.getServletPath(), req.getHeader("Authorization").length() > 20);
        chain.doFilter(req, res);
    }
}