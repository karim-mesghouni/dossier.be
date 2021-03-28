package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String description;
    @ManyToOne
    @JoinColumn
    Activity activity;

    @OneToMany(mappedBy = Job_.TASK,cascade = CascadeType.ALL)
    List<Job> jobs;
    @OneToMany(mappedBy = TaskState_.TASK,cascade = CascadeType.ALL)
    List<TaskSituation> situations;
    @OneToMany(mappedBy = TaskState_.TASK,cascade = CascadeType.ALL)
    List<TaskState> states;
    @OneToMany(mappedBy = FileTask_.TASK)
    List<FileTask> fileTasks;
}
