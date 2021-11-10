package com.softline.dossier.be.security.domain.Policy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component()
@RequiredArgsConstructor
@Slf4j(topic = "BasicPolicyEnforcement")
public class BasicPolicyEnforcement implements PolicyEnforcement {
    private final PolicyDefinition policyDefinition;

    @Override
    public boolean check(Object subject, Object resource, Object action, Object environment) {
        // add context
        SecurityAccessContext cxt = new SecurityAccessContext(subject, resource, action, environment);

        //finally, check if any of the rules are satisfied, otherwise return false.
        return checkRules(policyDefinition.getAllPolicyRules(), cxt);
    }

    private boolean checkRules(List<PolicyRule> matchedRules, SecurityAccessContext cxt) {
        for (PolicyRule rule : matchedRules) {
            try {
                return Boolean.TRUE.equals(rule.target.getValue(cxt, Boolean.class))
                        && Boolean.TRUE.equals(rule.condition.getValue(cxt, Boolean.class));
            } catch (Exception ex) {
                log.error("An error occurred while evaluating rule: {}", rule, ex);
            }
        }
        return false;
    }
}