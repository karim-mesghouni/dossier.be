package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileTask extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;
    @Type(type="text")
    String title;
    Long number;
    @OneToOne
    @JoinColumn
    DescriptionComment description;
    @OneToOne
    @JoinColumn
    ReturnedComment retour;
    @ManyToOne
    @JoinColumn
    FileActivity fileActivity;
    @ManyToOne
    @JoinColumn
    Task task;
    @Temporal(TemporalType.DATE)
    Date toStartDate;
    @Temporal(TemporalType.DATE)
    Date dueDate;
    @Temporal(TemporalType.DATE)
    Date startDate;
    @Temporal(TemporalType.DATE)
    Date endDate;
    @OneToOne
    Agent reporter;
    @OneToOne
    Agent assignedTo;
    @OneToMany(mappedBy = FileTaskSituation_.FILE_TASK,cascade = CascadeType.ALL)
    List<FileTaskSituation> fileTaskSituations;
    @ManyToOne
    @JoinColumn
    TaskState state;
    @Transient()
    FileTaskSituation currentFileTaskSituation;
}
