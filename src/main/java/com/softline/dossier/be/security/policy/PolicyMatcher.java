package com.softline.dossier.be.security.policy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "BasicPolicyEnforcement")
public class PolicyMatcher {
    private final JsonPolicyLoader policyStore;

    /**
     * check if any defined rule matches these ABAC attributes
     */
    public boolean check(Object subject, Object resource, Object action, Object environment) {
        // put all attributes in the context
        ABACContext cxt = new ABACContext(subject, resource, action, environment);

        // check if any of the defined rules matches the context attributes, otherwise return false.
        for (var rule : policyStore.getRules()) {
            try {
                if (Boolean.TRUE.equals(rule.target.getValue(cxt, Boolean.class))
                        && Boolean.TRUE.equals(rule.condition.getValue(cxt, Boolean.class))) {
                    return true;// if false check other rules in the loop
                }
            } catch (Exception ex) {
                log.error("An error occurred while evaluating {} {}", rule, ex.toString());
            }
        }
        return false;
    }
}
