package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE task_situation SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class TaskSituation extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    boolean initial;
    boolean Final;
    boolean block;
    @ManyToOne()
    @JoinColumn()
    Task task;
    @OneToMany(mappedBy = FileTaskSituation_.SITUATION)
    List<FileTaskSituation> fileTaskSituations;

    @Override
    public String toString()
    {
        return "TaskSituation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
