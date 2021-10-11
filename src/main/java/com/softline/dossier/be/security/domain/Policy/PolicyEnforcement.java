package com.softline.dossier.be.security.domain.Policy;

public interface PolicyEnforcement
{
    boolean check(Object subject, Object resource, Object action, Object environment);

}
