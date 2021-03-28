package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ActivityDataField extends  BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String fieldName;

    @Enumerated(EnumType.STRING)
    FieldType fieldType;

    String data;

    String groupName;
    @ManyToOne
    @JoinColumn()
    FileActivity fileActivity;

}
