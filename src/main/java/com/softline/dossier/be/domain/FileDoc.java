package com.softline.dossier.be.domain;

import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.FileDocumentEvent;
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
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE file_doc  SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class FileDoc extends BaseEntity {
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    File file;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    FileActivity fileActivity;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    Agent agent;
    String description;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String path;


    @PostPersist
    public void afterCreate() {
        new FileDocumentEvent(EntityEvent.Type.ADDED, this).fireToAll();
    }

    @PostUpdate
    public void afterUpdate() {
        new FileDocumentEvent(EntityEvent.Type.UPDATED, this).fireToAll();
    }

    @PostRemove
    public void afterDelete() {
        new FileDocumentEvent(EntityEvent.Type.DELETED, this).fireToAll();
    }
}
