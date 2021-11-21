package com.softline.dossier.be.SSE;

import com.softline.dossier.be.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

/**
 * an extension to the {@link SseEmitter} Response object,
 * the channel has a sessionId and a userId, and with some helper methods
 */
public class Channel extends SseEmitter {
    public final long sessionId;
    public final long userId;

    private Event<?> lastEvent;

    public Channel(long timeout, long sessionId, long userId) {
        super(timeout);
        this.sessionId = sessionId;
        this.userId = userId;
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
        return sessionId == channel.sessionId && userId == channel.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, userId);
    }

    @NotNull
    @Override
    public String toString() {
        return "Channel{" +
                "sessionId=" + sessionId +
                ", agentId=" + userId +
                '}';
    }

    /**
     * @return boolean value which tells if this channel can read the event
     */
    public boolean canRead(Event<?> event) {
        return event.isReadableByChannel(this);
    }
}
