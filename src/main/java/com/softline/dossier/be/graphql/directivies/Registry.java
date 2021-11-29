package com.softline.dossier.be.graphql.directivies;

import com.oembedler.moon.graphql.boot.SchemaDirective;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Registry {
    @Bean
    public SchemaDirective canDirective() {
        return new SchemaDirective("can", new CanDirective());
    }
}
