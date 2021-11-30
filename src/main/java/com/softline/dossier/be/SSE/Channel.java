package com.softline.dossier.be.SSE;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.events.Event;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

/**
 * an extension to the {@link SseEmitter} Response object,
 * the channel has a sessionId and a userId, and other helper methods
 */
public class Channel extends SseEmitter {
    public final long sessionId;
    public final HasId user;

    @Getter(AccessLevel.PROTECTED)
    private Event<?> lastEvent;

    /**
     * create a new SSE channel with unique combination of
     * (sessionId, userId) and with a defined timeout<br>
     * if no successful events were sent to this channel during
     * &lt;timeout&gt; amount of time the channel will be force closed
     *
     * @param timeout   timeout in milliseconds
     * @param sessionId the first unique identifier for this channel
     * @param user      the second unique identifier for this channel
     */
    public Channel(long timeout, long sessionId, @NotNull HasId user) {
        super(timeout);
        this.sessionId = sessionId;
        this.user = user;
    }

    /**
     * generate a random sessionId pass it to {@link #Channel(long, long, HasId)}
     */
    public Channel(long timeout, @NotNull HasId user) {
        this(timeout, (long) Math.floor(Math.random() * Long.MAX_VALUE), user);
    }

    /**
     * generate a random sessionId pass it to {@link #Channel(long, long, HasId)} with timeout of 1 hour
     */
    public Channel(@NotNull HasId user) {
        this(1000 * 60 * 60L, (long) Math.floor(Math.random() * Long.MAX_VALUE), user);
    }

    /**
     * register an event send attempt to this channel
     */
    public void setLastEvent(Event<?> lastEvent) {
        this.lastEvent = lastEvent;
    }

    /**
     * if sessionId and userId are equal returns true
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Channel channel = (Channel) o;
        return sessionId == channel.sessionId && user.getId() == channel.user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, user.getId());
    }

    @NotNull
    @Override
    public String toString() {
        return "Channel{" +
                "sessionId=" + sessionId +
                ", userId=" + user.getId() +
                '}';
    }

    /**
     * @return boolean value which tells if this channel has permission to read the given event
     */
    public boolean canRead(Event<?> event) {
        return event.isReadableByChannel(this);
    }
}
