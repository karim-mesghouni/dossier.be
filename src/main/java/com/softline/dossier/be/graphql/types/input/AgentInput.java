package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.security.domain.casl.CaslRawRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AgentInput
{

    Long id;
    String name;
    String username;

    String password;
    List<CaslRawRule> caslRules;
}
