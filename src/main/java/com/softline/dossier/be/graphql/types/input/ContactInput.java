package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ContactInput
{
    private Long id;
    private String name;
    private String phone;
    private String email;
}
