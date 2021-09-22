package com.softline.dossier.be.domain;


import com.softline.dossier.be.domain.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE ActivityField SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class ActivityField extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String fieldName;

    @Enumerated(EnumType.STRING)
    FieldType fieldType;
    @ManyToOne
    @JoinColumn()
    Activity activity;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn()
    ActivityFieldGroup group;
    @OneToOne
    @JoinColumn
    Activity activityBase;
    @Transient
    String groupName;
}
