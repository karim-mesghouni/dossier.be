package com.softline.dossier.be.SSE;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class Channel {
    public final long sessionId;
    public final long userId;

    /**
     * if sessionId and userId returns true
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

    @Override
    public String toString() {
        return "Channel{" +
                "sessionId=" + sessionId +
                ", agentId=" + userId +
                '}';
    }
}
