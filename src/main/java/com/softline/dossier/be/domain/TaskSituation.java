package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskSituation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    boolean initial;
    boolean Final;
    @ManyToOne()
    @JoinColumn()
    Task task;
    @OneToMany(mappedBy = FileTaskSituation_.SITUATION)
    List<FileTaskSituation> fileTaskSituations;
}
