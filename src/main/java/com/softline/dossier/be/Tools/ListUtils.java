package com.softline.dossier.be.Tools;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ListUtils {
    /**
     * calls the builder {count} times and returns the returned results of the builder as a collection
     *
     * @param <R> type of list items
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

    /**
     * return the first element that matches the filter, throws if no match found
     *
     * @param items  any collection
     * @param filter the filter function
     * @return first item which matches the filter
     * @throws NoSuchElementException if no item matches the filter found
     */
    @NotNull
    public static <T> T filterFirstStrict(@NotNull Collection<T> items, @NotNull Predicate<? super T> filter) throws NoSuchElementException {
        return items.stream().filter(filter).findFirst().orElseThrow();
    }

    /**
     * return the first element that matches the filter
     *
     * @param items  any collection
     * @param filter the filter function
     * @return optional first item which matches the filter
     */
    public static <T> Optional<T> filterFirst(@NotNull Collection<T> items, @NotNull Predicate<? super T> filter) {
        return items.stream().filter(filter).findFirst();
    }
}
