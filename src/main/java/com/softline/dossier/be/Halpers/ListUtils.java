package com.softline.dossier.be.Halpers;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ListUtils
{
    /**
     * calls the builder {count} times and returns the returned results of the builder as a collection
     *
     * @param count
     * @param builder
     * @param <R>     type of list items
     * @return a collection of {count} number of elements returned by the builder
     */
    @NotNull
    @SneakyThrows
    public static <R> List<R> createCount(int count, Callable<R> builder)
    {
        List<R> items = new ArrayList<>();
        while (count-- > 0) {
            items.add(builder.call());
        }
        return items;
    }
}
