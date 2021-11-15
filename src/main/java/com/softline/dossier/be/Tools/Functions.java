package com.softline.dossier.be.Tools;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@Slf4j
public class Functions {
    /**
     * call the callback(s) with the given value
     * and then return the value.<br>
     * useful to encapsulate objects that has methods which return void,
     * this allows to chain functions on the value in a single statement
     */
    @SafeVarargs // assert that `callbacks` variables are of type Consumer<T>
    public static <T> T tap(T value, @NotNull Consumer<T>... callbacks) {
        // apply all callbacks on the object
        for (var callable : callbacks)
            callable.accept(value);
        // then return the object
        return value;
    }

    /**
     * will encapsulate the action in a try-catch block
     *
     * @param action the action to be done
     * @return true if the action has run successfully otherwise false is returned
     */
    public static boolean safeRun(@NotNull Runnable action) {
        try {
            action.run();
            return true;
        } catch (Throwable e) {
            log.info("SafeRun: {}", e.getMessage());
        }
        return false;
    }

    /**
     * will encapsulate the action in a try-catch block,
     * runs the action only if the condition is met,
     * the condition call is also encapsulated with the action
     *
     * @param condition if it returned true the action will be called
     * @param action    the action to be done
     * @return false is returned if the condition or the action threw an exception otherwise true
     */
    public static boolean safeRun(@NotNull Callable<Boolean> condition, @NotNull Runnable action) {
        try {
            if (condition.call()) {
                action.run();
            }
            return true;
        } catch (Throwable e) {
            log.info("SafeRun: {}", e.getMessage());
            return false;
        }
    }

    /**
     * @return the value passed
     * @throws RuntimeException if the value (is null) or (is string and empty) or (is number and equal to 0) or (is Optional and isEmpty())
     */
    public static <T> T throwIfEmpty(T value) {
        if (Objects.isNull(value)
                || (value instanceof Boolean && !(Boolean) value)
                || (value instanceof CharSequence && ((CharSequence) value).length() == 0)
                || (value instanceof Optional && ((Optional<?>) value).isEmpty())
                || (value instanceof Number && ((Number) value).doubleValue() == 0)) {
            throw new RuntimeException("Empty");
        }
        return value;
    }

    /**
     * @param value         the value
     * @param lazyThrowable the exception producer to be thrown if the value is empty
     * @return the value passed
     * @throws E if the value (is null) or (is string and empty) or (is number and equal to 0) or (is Optional and isEmpty())
     */
    @SuppressWarnings("RedundantThrows")
    @SneakyThrows
    public static <T, E extends Throwable> T throwIfEmpty(T value, Callable<E> lazyThrowable) throws E {
        try {
            return throwIfEmpty(value);
        } catch (Throwable e) {
            throw lazyThrowable.call();
        }
    }

    /**
     * value is considered empty if the value (is null) or (is string and empty) or (is number and equal to 0) or (is Optional and isEmpty())
     *
     * @param producer a callback which will produce the value to be tested
     * @return true if the producer return value is not empty, false if the producer threw and exception or the returned value was empty,
     */
    public static boolean isEmpty(@NotNull Callable<Object> producer) {
        try {
            throwIfEmpty(producer.call());
            return true;
        } catch (Throwable e) {
            log.info("isEmpty, value was empty");
            return false;
        }
    }

    /**
     * produce a value or use fallback value if the producer returned empty value or the producer threw an exception,
     *
     * @param producer the value producer
     * @param fallback fallback value to be returned if the producer threw an exception or the producer return value was an empty value
     */
    public static <T> T safeValue(Callable<T> producer, T fallback) {
        try {
            return throwIfEmpty(producer.call());
        } catch (Throwable e) {
            log.info("safeValue: {}, returning fallback", e.getMessage());
            return fallback;
        }
    }

    /**
     * produce a value or fallback to null value if the producer returned empty value or the producer threw an exception,
     * Value is considered empty if the value (is null) or (is string and empty) or (is number and equal to 0) or (is Optional and isEmpty())
     *
     * @param producer the value producer
     * @return the producer return value or null if exception was thrown by the producer
     */
    @Nullable
    public static <T> T safeValue(@NotNull Callable<T> producer) {
        return safeValue(producer, null);
    }

    /**
     * will encapsulate the action in a try-catch block,
     * if the action fails the fallback action will be run with encapsulation
     *
     * @param action   the action to be done
     * @param fallback the fallback action to be run if the first action threw an exception
     * @return true if the action or the fallback has run successfully
     * if both the action and fallback failed false is returned
     */
    public static boolean safeRunWithFallback(@NotNull Runnable action, @NotNull Runnable fallback) {
        try {
            action.run();
            return true;
        } catch (Throwable e) {
            log.info("SafeRun: {}, running fallback action", e.getMessage());
            return safeRun(fallback);
        }
    }

    /**
     * call the action n number of times
     */
    public static void runNTimes(int n, @NotNull Runnable action) {
        for (int i = 0; i < n; i++)
            action.run();
    }
}
