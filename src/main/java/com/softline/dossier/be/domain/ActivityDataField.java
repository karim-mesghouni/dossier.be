package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.security.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

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

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    Agent lastModifiedBy;

    // used by graphql
    public Agent getLastModifiedBy() {
        if (lastModifiedBy == null) return getCreator();
        return lastModifiedBy;
    }
}
