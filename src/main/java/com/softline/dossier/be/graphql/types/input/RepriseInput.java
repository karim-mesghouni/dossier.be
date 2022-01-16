package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RepriseInput {

    Long id;
    int number;
    String cause;
    FileActivityInput fileActivity;

}
