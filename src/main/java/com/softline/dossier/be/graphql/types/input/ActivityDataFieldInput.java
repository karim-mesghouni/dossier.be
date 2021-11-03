package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.graphql.types.input.enums.FieldTypeInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityDataFieldInput {
    long id;
    String fieldName;

    FieldTypeInput fieldType;
    String data;
    String groupName;
    FileActivityInput fileActivity;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void tryCastData() throws NumberFormatException, DateTimeParseException {
        switch (fieldType) {
            case String:
                return;
            case Number:
                Double.valueOf(data);
                return;
            case Date:
                LocalDate.parse(data);
        }
    }
}
