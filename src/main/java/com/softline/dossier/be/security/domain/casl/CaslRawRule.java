package com.softline.dossier.be.security.domain.casl;

public class CaslRawRule {
    private final String action;
    private final String subject;

    public CaslRawRule(String action, String subject) {
        this.action = action;
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public String getAction() {
        return action;
    }
}
