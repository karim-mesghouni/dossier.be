package com.softline.dossier.be.Tools;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * contains static helper methods to convert between different date types
 */
public final class DateHelpers {
    private DateHelpers() {
    }

    public static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

    public static Date toDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LocalDate toLocalDate(LocalDateTime date) {
        return date.atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(LocalDate date) {
        return date.atStartOfDay();
    }
}
