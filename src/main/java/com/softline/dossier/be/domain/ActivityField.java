package com.softline.dossier.be.domain;


import com.softline.dossier.be.domain.enums.FieldType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ActivityField extends  BaseEntity{
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
