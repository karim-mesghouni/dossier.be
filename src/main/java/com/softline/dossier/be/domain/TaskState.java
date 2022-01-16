package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    public boolean isValid() {
        return getName().equalsIgnoreCase("Valide");
    }

}
