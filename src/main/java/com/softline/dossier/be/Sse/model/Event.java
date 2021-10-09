package com.softline.dossier.be.Sse.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
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

    public String getPayloadJson() throws JsonProcessingException
    {
        return new ObjectMapper().writeValueAsString(this.getPayload());
    }
}
