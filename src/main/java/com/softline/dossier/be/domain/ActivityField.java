package com.softline.dossier.be.domain;


import com.softline.dossier.be.domain.enums.FieldType;
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
@SQLDelete(sql = "UPDATE activity_field SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class ActivityField extends BaseEntity {
    String fieldName;

    @Enumerated(EnumType.STRING)
    FieldType fieldType;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    Activity activity;
    @ManyToOne(cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    ActivityFieldGroup group;
    @OneToOne(fetch = FetchType.LAZY)
    Activity activityBase;
}
