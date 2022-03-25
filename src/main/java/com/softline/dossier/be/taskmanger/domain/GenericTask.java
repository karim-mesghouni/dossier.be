package com.softline.dossier.be.taskmanger.domain;

import com.softline.dossier.be.domain.BaseEntity;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.taskmanger.converter.IntegerListToStringConverter;
import com.softline.dossier.be.taskmanger.converter.StringListToStringConverter;
import com.softline.dossier.be.taskmanger.enums.Periodicity;
import com.softline.dossier.be.taskmanger.enums.PeriodicityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@SQLDelete(sql = "UPDATE generic_task SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class GenericTask extends BaseEntity {
    String name;
    String description;
    @Enumerated(EnumType.STRING)
    Periodicity periodicity;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    TaskCategory category;

    @NotFound(action = NotFoundAction.IGNORE)
    @Enumerated(EnumType.STRING)
    PeriodicityType periodicityType;

    @Convert(converter = StringListToStringConverter.class)
    List<String> months;

    @Convert(converter = StringListToStringConverter.class)
    List<String> daysOfWeek;

    @Convert(converter = IntegerListToStringConverter.class)
    List<Integer> daysOfMonth;

    LocalDateTime toStartDate;
    LocalDateTime dueDate;

    @OneToOne
    @JoinColumn
    @NotFound(action = NotFoundAction.IGNORE)
    Agent reporter;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    Agent assignedTo;

//    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
//    List<TaskState> states;


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


