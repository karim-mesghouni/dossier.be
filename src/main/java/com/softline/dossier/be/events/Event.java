package com.softline.dossier.be.events;

import com.softline.dossier.be.SSE.EventController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A wrapper class for any events that will happen during the request cycle
 *
 * @param <T> the type of the event-payload (data type)
 */
public class Event<T> {
    private static Event<Long> pingEvent;
    @NotNull
    protected String type;
    @Nullable
    protected T payload;

    public Event(@NotNull String type, @Nullable T payload) {
        this.type = type;
        this.payload = payload;
    }

    /**
     * get the static ping event instance
     */
    @NotNull
    public static Event<Long> pingEvent() {
        if (pingEvent != null) return pingEvent;
        pingEvent = new Event<>("ping", System.currentTimeMillis());
        return pingEvent;
    }

    /**
     * @return payload converted to string, if payload is null empty string is returned
     */
    @NotNull
    public String getData() {
        // if the payload is of type JSONObject it will be parsed into a json string
        return payload != null ? payload.toString() : "";
    }

    @Override
    public String toString() {
        return "Event{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }

    /**
     * send this event to every open channel
     */
    public void fireToAll() {
        EventController.sendForAllChannels(this);
    }

    /**
     * send this event to every channel opened by the user
     */
    public void fireTo(long userId) {
        EventController.sendForUser(userId, this);
    }

    @NotNull
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event<?> event = (Event<?>) o;
        return type.equals(event.type) && Objects.equals(payload, event.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, payload);
    }
}
