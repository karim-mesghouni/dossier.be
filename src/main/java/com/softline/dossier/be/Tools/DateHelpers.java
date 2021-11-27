package com.softline.dossier.be.Tools;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Contract("_ -> new")
    public static Date toDate(@NotNull LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

    @NotNull
    @Contract("_ -> new")
    public static Date toDate(@NotNull LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault())
                .toInstant());
    }

    @Contract(pure = true)
    public static LocalDate toLocalDate(@NotNull Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @Contract(pure = true)
    public static LocalDate toLocalDate(@NotNull LocalDateTime date) {
        return date.toLocalDate();
    }

    @Contract(pure = true)
    public static LocalDateTime toLocalDateTime(@NotNull Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static LocalDateTime toLocalDateTime(@NotNull LocalDate date) {
        return date.atStartOfDay();
    }
}
