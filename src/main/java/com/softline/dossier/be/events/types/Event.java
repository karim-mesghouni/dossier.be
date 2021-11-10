package com.softline.dossier.be.events.types;

import com.softline.dossier.be.SSE.EventController;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A wrapper class for any events that will happen during the request cycle
 *
 * @param <T> the type of the event payload(data)
 */
@Data
@NoArgsConstructor
@Builder
public class Event<T> {
    protected String event;
    protected T payload;

    public Event(String event, T payload) {
        this.event = event;
        this.payload = payload;
    }

    public String getPayloadJson() {
        // if the object is of type JSONObject it will be parsed into a json string
        return this.getPayload().toString();
    }

    @Override
    public String toString() {
        return "Event{" +
                "event='" + event + '\'' +
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
}
