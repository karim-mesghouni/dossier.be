package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

@Getter
public class CommuneInput implements HasId {

    Long id;

    String name;

    String postalCode;

    String INSEECode;
}
