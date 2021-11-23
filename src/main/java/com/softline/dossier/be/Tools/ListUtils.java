package com.softline.dossier.be.Tools;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ListUtils {
    /**
     * calls the builder {count} times and returns the returned results of the builder as a collection
     * @param <R>     type of list items
     * @return a list of {count} number of elements returned by the builder
     */
    @NotNull
    public static <R> List<R> createCount(int count, Supplier<R> builder) {
        List<R> items = new ArrayList<>();
        while (count-- > 0) {
            items.add(builder.get());
        }
        return items;
    }
}
