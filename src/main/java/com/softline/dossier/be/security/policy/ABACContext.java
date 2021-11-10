package com.softline.dossier.be.security.policy;

public class ABACContext {
    public Object subject;
    public Object resource;
    public Object action;
    public Object environment;

    public ABACContext(Object subject, Object resource, Object action, Object environment) {
        this.subject = subject;
        this.resource = resource;
        this.action = action;
        this.environment = environment;
    }
}
