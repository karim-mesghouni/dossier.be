package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.ControlSheet;
import lombok.Getter;

@Getter
public class ControlSheetInput extends Input<ControlSheet> implements HasId {
    Class<ControlSheet> mappingTarget = ControlSheet.class;
    Long id;
}
