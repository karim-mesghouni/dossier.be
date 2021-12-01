package com.softline.dossier.be.events;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * A wrapper class for any entity action events that will happen during the request life-cycle
 */
@Slf4j(topic = "EntityEvent")
public abstract class EntityEvent<E> extends Event<JSONObject> implements Serializable {
    protected E entity;

    public EntityEvent(String type, E entity) {
        super(type, new JSONObject());
        this.entity = entity;
    }

    public void addData(@NotNull String attribute, @Nullable Object value) {
        if (!Objects.requireNonNull(payload).has(attribute)) {
            try {
                payload.put(attribute, value);
            } catch (JSONException e) {
                log.error("{}: failed to put json data of type : {}", getClass().getName(), value == null ? "null" : value.getClass().getName());
            }
        }
    }


    public enum Type {
        TRASHED,
        UPDATED,
        RECOVERED,
        DELETED,
        ADDED;

        // convert enum name to CamelCase
        public String toString() {
            return ("" + this.name().charAt(0)).toUpperCase(Locale.ROOT) + this.name().substring(1).toLowerCase(Locale.ROOT);
        }
    }
}
