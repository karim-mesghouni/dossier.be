package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE ActivityDataField SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class ActivityDataField extends BaseEntity {

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
