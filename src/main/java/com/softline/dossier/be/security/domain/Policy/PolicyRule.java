package com.softline.dossier.be.security.domain.Policy;

import lombok.Data;
import org.springframework.expression.Expression;

@Data
public class PolicyRule
{

    private String name;
    private String description;
    /*
     * Boolean SpEL expression. If evaluated to true, then this rule is applied to the request access context.
     */
    private Expression target;

    /*
     * Boolean SpEL expression, if evaluated to true, then access granted.
     */
    private Expression condition;

    public PolicyRule()
    {

    }

    public PolicyRule(String name, String description, Expression target, Expression condition)
    {
        this(target, condition);
        this.name = name;
        this.description = description;
    }

    public PolicyRule(Expression target, Expression condition)
    {
        super();
        this.target = target;
        this.condition = condition;
    }
}
