package com.softline.dossier.be.Halpers;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

@Slf4j
public class Functions
{
    /**
     * call the callback with the given value
     * and then return the value.<br>
     * useful if the value does not return itself,
     * this allows to chain functions of the value
     */
    @SafeVarargs // assert that `callbacks` variables are of type Consumer<T>
    public static <T> T tap(T value, Consumer<T>... callbacks)
    {
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
    public static boolean safeRun(Runnable action)
    {
        try {
            action.run();
            return true;
        } catch (Throwable e) {
            log.warn("SafeRun: {}", e.getMessage());
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
    public static boolean safeRun(Callable<Boolean> condition, Runnable action)
    {
        try {
            if (condition.call()) {
                action.run();
            }
            return true;
        } catch (Throwable e) {
            log.warn("SafeRun: {}", e.getMessage());
            return false;
        }
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
    public static boolean safeRun(Runnable action, Runnable fallback)
    {
        try {
            action.run();
            return true;
        } catch (Throwable e) {
            log.warn("SafeRun: {}", e.getMessage());
            return safeRun(fallback);
        }
    }
}
