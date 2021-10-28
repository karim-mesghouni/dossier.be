package com.softline.dossier.be.events.types;

import lombok.SneakyThrows;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

public abstract class EntityEvent extends Event<JSONObject> implements Serializable
{
    public EntityEvent(String type)
    {
        super();
        event = type;
        payload = new JSONObject();
    }

    @SneakyThrows
    public void addData(String attribute, Object value)
    {
        if (!payload.has(attribute)) {
            payload.put(attribute, value);
        }
    }

    public enum Event
    {
        TRASHED,
        UPDATED,
        RECOVERED,
        DELETED,
        ADDED;

        public String toString()
        {
            return ("" + this.name().charAt(0)).toUpperCase(Locale.ROOT) + this.name().substring(1).toLowerCase(Locale.ROOT);
        }
    }
}
