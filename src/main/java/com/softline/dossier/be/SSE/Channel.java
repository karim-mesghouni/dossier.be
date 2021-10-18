package com.softline.dossier.be.SSE;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class Channel
{
    public final long sessionId;
    public final long agentId;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Channel channel = (Channel) o;
        return sessionId == channel.sessionId && agentId == channel.agentId;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(sessionId, agentId);
    }

    @Override
    public String toString()
    {
        return "Channel{" +
                "sessionId=" + sessionId +
                ", agentId=" + agentId +
                '}';
    }
}
