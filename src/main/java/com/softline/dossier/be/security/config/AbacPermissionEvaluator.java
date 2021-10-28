package com.softline.dossier.be.security.config;


import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.security.domain.Policy.PolicyEnforcement;
import graphql.schema.TypeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AbacPermissionEvaluator implements PermissionEvaluator
{
    @Autowired
    PolicyEnforcement policy;
    private final EntityManager entityManager;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
    {
        //Getting subject
        Object user = authentication.getPrincipal();
        //Getting environment
        Map<String, Object> environment = new HashMap<>();
        environment.put("time", new Date());

        return policy.check(user, targetDomainObject, permission, environment);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission)
    {
        Object target = null;
        if(targetType.equals("File")){
            target = entityManager.find(File.class, targetId);
        }else if(targetType.equals("FileActivity")){
            target = entityManager.find(FileActivity.class, targetId);
        }
        return hasPermission(authentication, target, permission);
    }
}
