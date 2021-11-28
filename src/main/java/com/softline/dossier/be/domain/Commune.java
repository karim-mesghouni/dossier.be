package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@SuperBuilder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class Commune implements HasId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String department;
    private String postalCode;
    private String INSEECode;
}
