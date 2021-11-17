package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.FileActivityDataFieldEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@SuperBuilder
@Data

@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE activity_data_field SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class ActivityDataField extends BaseEntity {
    String fieldName;

    @Enumerated(EnumType.STRING)
    FieldType fieldType;

    String data;

    String groupName;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    FileActivity fileActivity;


    @PostPersist
    private void afterCreate() {
        new FileActivityDataFieldEvent(EntityEvent.Type.ADDED, this).fireToAll();
    }

    @PostUpdate
    private void afterUpdate() {
        new FileActivityDataFieldEvent(EntityEvent.Type.UPDATED, this).fireToAll();
    }

    @PostRemove
    private void afterDelete() {
        new FileActivityDataFieldEvent(EntityEvent.Type.DELETED, this).fireToAll();
    }
}
