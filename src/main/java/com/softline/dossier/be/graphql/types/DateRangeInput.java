package com.softline.dossier.be.graphql.types;

import java.time.LocalDate;
import java.util.Optional;

public class DateRangeInput {
    // LocalDate.MAX is out of bounds for database date type
    private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 30);
    private static final LocalDate MIN_DATE = LocalDate.of(1, 1, 1);

    public LocalDate from;
    public LocalDate to;

    public LocalDate getTo() {
        return Optional.ofNullable(to)
                .orElse(MAX_DATE);
    }

    public LocalDate getFrom() {
        return Optional.ofNullable(from)
                .orElse(MIN_DATE);
    }
}