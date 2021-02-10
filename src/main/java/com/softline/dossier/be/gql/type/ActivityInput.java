package com.softline.dossier.be.gql.type;

import lombok.Data;

import java.util.List;
@Data
public class ActivityInput {

    long id;
    String   name;
    String  description;
    List<ActivityFieldInput> fields;
}
