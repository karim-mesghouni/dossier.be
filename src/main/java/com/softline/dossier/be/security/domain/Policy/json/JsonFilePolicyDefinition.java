package com.softline.dossier.be.security.domain.Policy.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.softline.dossier.be.security.domain.Policy.PolicyDefinition;
import com.softline.dossier.be.security.domain.Policy.PolicyRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = "PolicyLoader")
public class JsonFilePolicyDefinition implements PolicyDefinition {
    private List<PolicyRule> rules;

    @PostConstruct
    private void init() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Expression.class, new SpelDeserializer());
        mapper.registerModule(module);
        try {
            rules = new ArrayList<>();

            ClassLoader cl = this.getClass().getClassLoader();
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
            Resource[] resources = resolver.getResources("classpath:/policy/*.policy.json");
            for (Resource resource : resources) {
                try {
                    log.debug("loading policy file: {}", resource.getFile());
                    rules.addAll(Arrays.stream(mapper.readValue(resource.getInputStream(), PolicyRule[].class)).collect(Collectors.toList()));
                } catch (IOException e) {
                    log.error("An error occurred while parsing the policy file.", e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to load policy files.", e);
        }
        if (rules.isEmpty()) {
            log.error("No policy json file was found in the class path: /policy/*.policy.json");
        }
    }

    @Override
    public List<PolicyRule> getAllPolicyRules() {
        return rules;
    }

}
