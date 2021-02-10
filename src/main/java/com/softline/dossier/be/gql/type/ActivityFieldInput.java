package com.softline.dossier.be.gql.type;

import com.softline.dossier.be.domain.enums.FieldType;
import lombok.Data;

@Data
public class ActivityFieldInput {
    long id;
    String  fieldName;
    FieldType fieldType;
}
