package com.softline.dossier.be.domain;

import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.MessageEvent;
import com.softline.dossier.be.security.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;

@SuperBuilder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE Message SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate
@SelectBeforeUpdate
public class Message extends BaseEntity {
    @Column(columnDefinition = "boolean default false")
    boolean readMessage;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    Comment comment;
    @ManyToOne
    @JoinColumn
    @NotFound(action = NotFoundAction.IGNORE)
    Agent targetAgent;

    /**
     * will be set to true when the message is first created, and value will be false for messages pulled from the database
     * this will tell if we need to send an event or not when we update the related comment
     */
    @Transient
    boolean parsedNow;

    @PostRemove
    public void afterRemove() {
        new MessageEvent(EntityEvent.Type.DELETED, this).fireTo(getTargetAgent());
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", targetAgent=" + targetAgent +
                ", comment=" + comment +
                ", readMessage=" + readMessage +
                '}';
    }

    /**
     * mark message as read
     */
    public void read() {
        setReadMessage(true);
    }
}
