package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.events.EntityEvent;

public class ClientEvent extends EntityEvent<Client> {

    public ClientEvent(Type type, Client client) {
        super("client" + type, client);
        addData("clientId", client.getId());
    }
}
