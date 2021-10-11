package com.softline.dossier.be.config;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.softline.dossier.be.graphql.types.FileDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class SchemaParserConfig
{

    @Bean
    public SchemaParserDictionary schemaParserDictionary()
    {
        return new SchemaParserDictionary()
                .add(FileDTO.class);
    }
}
