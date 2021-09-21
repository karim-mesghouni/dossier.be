package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE TaskState SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class TaskState extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    @ManyToOne()
    @JoinColumn()
    Task task;
    @OneToMany(mappedBy = FileTask_.STATE)
    List<FileTask> fileTasks;

    @Override
    public String toString() {
        return "TaskState{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
