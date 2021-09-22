package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BlockingInput {

    long id;


    FileTaskSituationInput state;

    BlockingLockingAddressInput lockingAddress;

    BlockingQualificationInput qualification;

    BlockingLabelInput label;
    LocalDateTime date;
    private String explication;
}
