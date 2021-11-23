package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.BlockingLabel;
import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

import java.util.List;

@Getter
public class BlockingLabelInput extends Input<BlockingLabel> implements HasId {
    Class<BlockingLabel> mappingTarget = BlockingLabel.class;
    long id;

    List<BlockingInput> blocking;
    String name;
}
