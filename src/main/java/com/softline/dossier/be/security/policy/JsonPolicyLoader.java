package com.softline.dossier.be.security.policy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = "PolicyLoader")
public class JsonPolicyLoader {
    // these rules are loaded once on the application boot phase
    private List<PolicyRule> rules;

    /**
     * load from the resource directory "/policy/" all json policy files that end with *.policy.json,
     */
    @PostConstruct // will be called once when the application starts
    private void init() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Expression.class, new StringExpressionDeserializer());
        mapper.registerModule(module);
        try {
            rules = new ArrayList<>();

            ClassLoader cl = this.getClass().getClassLoader();
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
            Resource[] resources = resolver.getResources("classpath:/policy/*.policy.json");
            for (Resource resource : resources) {
                try {
                    log.info("loading policy file: {}", resource.getFile());
                    rules.addAll(Arrays.stream(mapper.readValue(resource.getInputStream(), PolicyRule[].class)).collect(Collectors.toList()));
                } catch (Exception e) {
                    log.error("An error occurred while parsing the policy file.", e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to load policy files.", e);
        }
        if (rules.isEmpty()) {
            log.error("No policy json file was found in the resources path: /policy/*.policy.json");
        }
    }

    public List<PolicyRule> getRules() {
        return rules;
    }

}
