package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@SuperBuilder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE returned_cause SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class ReturnedCause extends BaseEntity {
    String name;
    @OneToMany(mappedBy = "returnedCause")
    List<FileTask> fileTasks;
}
