package com.softline.dossier.be.Tools;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class Functions {
    /**
     * pass the object to each consumer
     * and then return the object.<br>
     * useful to encapsulate objects that has methods which return void,
     * this allows to chain functions on the object in a single statement
     *
     * @return the same object T
     */
    @SafeVarargs
    public static <T> T tap(T object, @NotNull Consumer<T>... callbacks) {
        // apply all callbacks on the object
        Arrays.stream(callbacks).forEach(callable -> callable.accept(object));
        // then return the object
        return object;
    }

    /**
     * will encapsulate the action in a try-catch block<br>
     * useful to avoid null pointer exception
     *
     * @param action the action to be done
     * @return true if the action has run successfully otherwise false is returned
     */
    public static boolean safeRun(@NotNull Runnable action) {
        try {
            action.run();
            return true;
        } catch (Throwable e) {
            if (log.isDebugEnabled())
                log.info("SafeRun: {}", e.getMessage());
        }
        return false;
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
            if (log.isDebugEnabled())
                log.info("SafeRun: {}, running fallback action", e.getMessage());
            return safeRun(fallback);
        }
    }

    /**
     * will encapsulate the action in a try-catch block,
     * runs the action only if the condition is met,
     * the condition call is also encapsulated with the action<br>
     * useful to avoid null pointer exception
     *
     * @param condition if it returned true the action will be called
     * @param action    the action to be done
     * @return false is returned if the condition or the action threw an exception otherwise true
     */
    public static boolean safeRunIf(@NotNull Supplier<Boolean> condition, @NotNull Runnable action) {
        try {
            if (condition.get()) {
                action.run();
            }
            return true;
        } catch (Throwable e) {
            if (log.isDebugEnabled())
                log.info("SafeRun: {}", e.getMessage());
            return false;
        }
    }

    /**
     * the value is considered empty if any of these conditions are met<br>
     * the value (is null) or (is string and length is 0) or (is number and equal to 0) or (is Optional and isEmpty()) or (is boolean and equals false)
     *
     * @return the value passed
     * @throws RuntimeException if the value was considered empty
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
     * throws an exception E if the value is {@link Functions#throwIfEmpty(Object) empty} or if the value retrieval failed
     */
    @SuppressWarnings("RedundantThrows")
    public static <T, E extends Throwable> T throwIfSuppliedEmpty(Supplier<T> supplier, Supplier<E> throwable) throws E {
        try {
            return throwIfEmpty(supplier.get());
        } catch (Throwable e) {
            throw throwable.get();
        }
    }


    /**
     * throws an exception E if the value is {@link Functions#throwIfEmpty(Object) empty}
     */
    @SuppressWarnings("RedundantThrows")
    public static <T, E extends Throwable> T throwIfEmpty(T value, Supplier<E> throwable) throws E {
        try {
            return throwIfEmpty(value);
        } catch (Throwable e) {
            throw throwable.get();
        }
    }

    /**
     * returns false if the producer produced an {@link Functions#throwIfEmpty(Object) empty value}
     */
    public static boolean isEmpty(@NotNull Supplier<?> producer) {
        try {
            throwIfEmpty(producer.get());
            return false;
        } catch (Throwable e) {
            if (log.isDebugEnabled())
                log.info("isEmpty, value was empty");
            return true;
        }
    }

    /**
     * value is considered empty if the value (is null) or (is string and empty) or (is number and equal to 0) or (is Optional and isEmpty())
     *
     * @param producer a callback which will produce the value to be tested
     * @return true if the producer return value is not empty, false if the producer threw and exception or the returned value was empty,
     */
    public static boolean notEmpty(@NotNull Supplier<?> producer) {
        return !isEmpty(producer);
    }

    /**
     * produce a value or use fallback value if the producer returned empty value or the producer threw an exception,<br>
     * useful to avoid null pointer exception
     *
     * @param producer the value producer
     * @param fallback fallback value to be returned if the producer threw an exception or the producer return value was an empty value
     */
    public static <T> T safeValue(Supplier<T> producer, T fallback) {
        try {
            return throwIfEmpty(producer.get());
        } catch (Throwable e) {
            if (log.isDebugEnabled())
                log.info("safeValue: {}, returning fallback", e.getMessage());
            return fallback;
        }
    }

    /**
     * produce a value or use fallback value if the producer returned empty value or the producer threw an exception,
     *
     * @param producer the value producer
     * @param fallback fallback value to be returned if the producer threw an exception or the producer return value was an empty value
     */
    public static <T> T safeSupplied(Supplier<T> producer, Supplier<T> fallback) {
        try {
            return throwIfEmpty(producer.get());
        } catch (Throwable e) {
            if (log.isDebugEnabled())
                log.info("safeValue: {}, returning fallback", e.getMessage());
            return fallback.get();
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
    public static <T> T safeValue(@NotNull Supplier<T> producer) {
        return safeValue(producer, null);
    }

    /**
     * call the action n number of times
     */
    public static void runNTimes(int n, @NotNull Runnable action) {
        for (int i = 0; i < n; i++)
            action.run();
    }

    /**
     * run the action and catch any exception and convert them into RuntimeException
     */
    public static void wrap(Runnable action) throws RuntimeException {
        try {
            action.run();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * return the value of the callback or catch any exception and convert them into RuntimeException<br>
     * can be used to ignore Exceptions
     */
    public static <T> T wrap(Callable<T> action) throws RuntimeException {
        try {
            return action.call();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * return the value of the callback or catch any exception and convert them into a new Exception with a custom message<br>
     * can be used to change the exception message
     */
    public static <T, E extends Throwable> T wrap(Callable<T> action, @NotNull Function<Throwable, E> exception) throws E {
        try {
            return action.call();
        } catch (Throwable e) {
            throw exception.apply(e);
        }
    }

    /**
     * converts Exceptions into RuntimeExceptions
     */
    public static void wrap(UnsafeRunnable action) throws RuntimeException {
        try {
            action.run();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface UnsafeRunnable {
        void run() throws Throwable;
    }
}

