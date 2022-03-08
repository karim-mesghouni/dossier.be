package com.softline.dossier.be.task_management.domain;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.task_management.enums.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;


@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE GenericTask SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class GenericTask extends BaseEntity {
    String name;
    @NotFound(action = NotFoundAction.IGNORE)
    String description;
    Periodicity periodicity;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    TaskCategory category;

    @NotFound(action = NotFoundAction.IGNORE)
    @Enumerated(EnumType.STRING)
    PeriodicityType periodicityType;

    @NotFound(action = NotFoundAction.IGNORE)
    @Enumerated(EnumType.STRING)
    List<Months> months;

    @NotFound(action = NotFoundAction.IGNORE)
    @Enumerated(EnumType.STRING)
    List<DaysOfWeek> daysOfWeek;

    @NotFound(action = NotFoundAction.IGNORE)
    List<Integer> daysOfMonth;

    LocalDateTime toStartDate;
    LocalDateTime dueDate;
    LocalDateTime startDate;
    LocalDateTime endDate;

//    @OneToOne
//    @JoinColumn
//    @NotFound(action = NotFoundAction.IGNORE)
//    Agent reporter;
//    @OneToMany
//    @JoinColumn
//    @NotFound(action = NotFoundAction.IGNORE)
//    Agent assignedTo;


//    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
//    List<TaskState> states;


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}


