package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.BlockingQualification;
import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

import java.util.List;

@Getter
public class BlockingQualificationInput extends Input<BlockingQualification> implements HasId {
    Class<BlockingQualification> mappingTarget = BlockingQualification.class;

    long id;
    List<BlockingInput> blocking;
    String name;
}
