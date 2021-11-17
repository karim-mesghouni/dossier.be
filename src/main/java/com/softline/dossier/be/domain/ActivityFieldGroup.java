package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@SQLDelete(sql = "UPDATE activity_field_group SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")

public class ActivityFieldGroup extends BaseEntity {
    String name;
}
