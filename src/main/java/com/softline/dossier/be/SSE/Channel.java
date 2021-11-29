package com.softline.dossier.be.SSE;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

/**
 * an extension to the {@link SseEmitter} Response object,
 * the channel has a sessionId and a userId, and other helper methods
 */
public class Channel extends SseEmitter {
    public final long sessionId;
    public final HasId user;

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
     * @return the last attempted event send to this channel
     */
    @Nullable
    public Event<?> getLastEvent() {
        return lastEvent;
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
