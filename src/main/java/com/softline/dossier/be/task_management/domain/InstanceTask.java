package com.softline.dossier.be.task_management.domain;


import com.softline.dossier.be.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE InstanceTask SET deleted=true WHERE id=?")
@Where(clause = "deleted = false ")
@DynamicUpdate// only generate sql statement for changed
public class InstanceTask extends BaseEntity {
    String name;
    String description;


}
