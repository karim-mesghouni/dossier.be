package com.softline.dossier.be.events;

import com.google.errorprone.annotations.ForOverride;
import com.softline.dossier.be.SSE.Channel;
import com.softline.dossier.be.SSE.EventController;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * A wrapper class for any events that will happen during the request cycle
 *
 * @param <T> the type of the event-payload (data type)
 */
@Slf4j(topic = "EventObject")
public class Event<T> implements Serializable {
    private static final long serialVersionUID = 5689867320034832774L;

    @NotNull
    protected String type;
    @Nullable
    protected T payload;

    private static Event<Long> pingEvent;

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
     * flush the database and send this event to every open channel
     */
    public void fireToAll() {
        EventController.sendForAllChannels(this);
    }

    /**
     * flush the database and send this event to every channel opened by the user
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

    /**
     * @return boolean value which tells if the channel has permission to read this event
     */
    public boolean isReadableByChannel(Channel channel) {
        try {
            return Boolean.TRUE.equals(getPermissionEvaluator(channel).call());// convert null to false
        } catch (Exception e) {
            log.error("Event.getPermission() failed to get permissionEvaluator result", e);
            return false;
        }
    }

    /**
     * returns a callback which  when called should produce
     * a boolean which tells if the channel has permission to read this event according to the passed channel.
     * default callable return value is true
     */
    @ForOverride
    protected Callable<Boolean> getPermissionEvaluator(Channel channel) {
        return () -> true;
    }
}
