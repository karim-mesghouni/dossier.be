package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE task_state SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class TaskState extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    @ManyToOne()
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn()
    Task task;
    @OneToMany(mappedBy = "state")
    List<FileTask> fileTasks;

    @Override
    public String toString() {
        return "TaskState{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
