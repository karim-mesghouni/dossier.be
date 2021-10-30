package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VisAVisInput {

    long id;
    String name;
    String email;
    String phone;

    ClientInput client;

    BlockingLockingAddressInput lockingAddress;
}
