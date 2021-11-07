package com.softline.dossier.be.events.types;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Locale;

@Slf4j(topic = "EntityEvent")
public abstract class EntityEvent extends Event<JSONObject> implements Serializable {
    public EntityEvent(String type) {
        super();
        event = type;
        payload = new JSONObject();
    }

    public void addData(String attribute, @Nullable Object value) {
        if (!payload.has(attribute)) {
            try {
                payload.put(attribute, value);
            } catch (JSONException e) {
                e.printStackTrace();
                log.error("{}: failed to put json data of type : {}", getClass().getName(), value.getClass().getName());
            }
        }
    }

    public enum Event {
        TRASHED,
        UPDATED,
        RECOVERED,
        DELETED,
        ADDED;

        public String toString() {
            return ("" + this.name().charAt(0)).toUpperCase(Locale.ROOT) + this.name().substring(1).toLowerCase(Locale.ROOT);
        }
    }
}
