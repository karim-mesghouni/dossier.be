package com.softline.dossier.be.events;

import com.google.errorprone.annotations.ForOverride;
import com.softline.dossier.be.SSE.Channel;
import com.softline.dossier.be.SSE.EventController;
import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.configurationprocessor.json.JSONObject;

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
        pingEvent = new Event<>("ping", null);
        return pingEvent;
    }

    /**
     * get the payload data as string (call {@link Object#toString()} method)<br>
     * if the payload is of type {@link JSONObject} it will be parsed into a json string
     *
     * @return payload converted to string, if payload is null empty string is returned
     */
    @NotNull
    public String getData() {
        return payload != null
                ? payload.toString()
                : "";
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
    public void fireTo(HasId user) {
        EventController.sendForUser(user, this);
    }

    /**
     * send this event to every channel opened by the user
     *
     * @param userId the id of the user
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
        if (this == o) return true;// if same reference
        if (o == null || getClass() != o.getClass()) return false;// if not same class
        Event<?> event = (Event<?>) o;
        return Objects.equals(type, event.type) && Objects.equals(payload, event.payload);// same type and same payload
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
            if (log.isErrorEnabled())
                log.error("Event.getPermission() failed to get permissionEvaluator result", e);
            return false;
        }
    }

    /**
     * returns a callback which  when called should produce
     * a boolean which tells if the channel has permission to read this event according to the passed channel.
     * default callable return value is true
     */
    @SuppressWarnings("RedundantThrows")
    @ForOverride
    @NotNull
    protected Callable<Boolean> getPermissionEvaluator(Channel channel) throws Exception {
        return () -> true;
    }
}
