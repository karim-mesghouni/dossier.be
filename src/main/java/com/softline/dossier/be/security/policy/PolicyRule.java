package com.softline.dossier.be.security.policy;

import org.springframework.expression.Expression;

public class PolicyRule {
    public String name;
    public String description;
    /*
     * Boolean SpEL expression. If evaluated to true, then this rule is applied to the request access context.
     */
    public Expression target;
    /*
     * Boolean SpEL expression, if evaluated to true, then access granted.
     */
    public Expression condition;


    @Override
    public String toString() {
        return "PolicyRule{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", target=" + target.getExpressionString() +
                ", condition=" + condition.getExpressionString() +
                '}';
    }
}
