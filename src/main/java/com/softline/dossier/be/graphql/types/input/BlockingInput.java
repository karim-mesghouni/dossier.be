package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Blocking;
import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BlockingInput extends Input<Blocking> implements HasId {
    Class<Blocking> mappingTarget = Blocking.class;
    Long id;
    FileTaskSituationInput state;

    BlockingLockingAddressInput lockingAddress;

    BlockingQualificationInput qualification;

    BlockingLabelInput label;
    LocalDateTime date;
    String explication;
    LocalDateTime dateUnBlocked;
}
