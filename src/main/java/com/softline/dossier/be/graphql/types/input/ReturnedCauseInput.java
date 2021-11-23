package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.ReturnedCause;
import lombok.Getter;

@Getter
public class ReturnedCauseInput extends Input<ReturnedCause> implements HasId {
    Class<ReturnedCause> mappingTarget = ReturnedCause.class;

    long id;
    String name;

}
