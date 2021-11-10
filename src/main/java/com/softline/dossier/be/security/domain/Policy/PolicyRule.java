package com.softline.dossier.be.security.domain.Policy;

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

    // required by object mapper
    public PolicyRule() {

    }

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
