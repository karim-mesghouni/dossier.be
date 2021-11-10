package com.softline.dossier.be.domain;

import com.softline.dossier.be.events.entities.MessageEvent;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.security.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;

@SuperBuilder
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE Message SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate
@SelectBeforeUpdate
public class Message extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
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
     * send event to the mentioned user
     */
    @PostPersist
    public void afterCreate() {
        new MessageEvent(EntityEvent.Type.ADDED, this).fireTo(getTargetAgent().getId());
    }

    /**
     * send event to the mentioned user
     */
    @PostRemove
    public void afterDelete() {
        new MessageEvent(EntityEvent.Type.DELETED, this).fireTo(getTargetAgent().getId());
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", comment=" + comment +
                ", targetAgent=" + targetAgent +
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
