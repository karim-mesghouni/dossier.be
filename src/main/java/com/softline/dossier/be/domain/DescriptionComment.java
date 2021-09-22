package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Description")
@SuperBuilder
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE Comment SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class DescriptionComment extends Comment {

}
