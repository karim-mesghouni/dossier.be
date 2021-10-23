package com.softline.dossier.be.SSE;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
public class Event implements Serializable
{
    private String name;
    private Object payload;

    public Event(String name, Object payload)
    {
        this.name = name;
        this.payload = payload;
    }

    public String getPayloadJson()
    {
        // if the object is of type JSONObject it will be parsed into a json string
        return this.getPayload().toString();
    }

    public String toString()
    {
        return "name: " + name + ", payload: " + payload.toString();
    }
}
