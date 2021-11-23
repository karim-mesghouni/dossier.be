package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.BlockingLockingAddress;
import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

import java.util.List;

@Getter
public class BlockingLockingAddressInput extends Input<BlockingLockingAddress> implements HasId {
    Class<BlockingLockingAddress> mappingTarget = BlockingLockingAddress.class;

    long id;
    List<BlockingInput> blocking;
    List<VisAVisInput> visAVis;
    String address;
}
