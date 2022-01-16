package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.VisAVis;
import lombok.Getter;

@Getter
public class VisAVisInput extends Input<VisAVis> implements HasId {
    Class<VisAVis> mappingTarget = VisAVis.class;

    Long id;
    String name;
    String email;
    String phone;

    ClientInput client;

    BlockingLockingAddressInput lockingAddress;
}
