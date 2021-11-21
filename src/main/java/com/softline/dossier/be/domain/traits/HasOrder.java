package com.softline.dossier.be.domain.traits;

public interface HasOrder {
    default void incrementOrder() {
        setOrder(getOrder() + 1);
    }

    default void decrementOrder() {
        setOrder(getOrder() - 1);
    }

    long getOrder();

    void setOrder(long value);
}
