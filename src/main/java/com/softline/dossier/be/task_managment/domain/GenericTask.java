package com.softline.dossier.be.task_managment.domain;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.task_managment.enums.Periodicity;
import com.softline.dossier.be.task_managment.enums.PeriodicityType;
import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;


@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE Task SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class GenericTask extends BaseEntity {
    String name;
    String description;
    Periodicity periodicity;
    @Type(type = "com.softline.dossier.be.task_managment.domain.TaskCategorys")
    TaskCategory category;
    PeriodicityType periodicityType;


    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    List<TaskState> states;


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}


