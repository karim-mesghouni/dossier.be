package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BlockingInput  {

    long id;


    FilePhaseStateInput state;

    BlockingLockingAddressInput lockingAddress;

    BlockingQualificationInput qualification;

    BlockingLabelInput label;
}
