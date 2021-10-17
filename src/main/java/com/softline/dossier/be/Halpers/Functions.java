package com.softline.dossier.be.Halpers;

import java.util.function.Consumer;

public class Functions
{
    /**
     * call the callback with the given value
     * and then return the value
     * useful if the value does not return itself
     * this allows to chain functions of the value
     */
    @SafeVarargs // assert that `callbacks` variables are of type Consumer<T>
    public  static <T> T tap(T value, Consumer<T> ...callbacks){
        for(var callable: callbacks)
            callable.accept(value);
        return value;
    }
}
