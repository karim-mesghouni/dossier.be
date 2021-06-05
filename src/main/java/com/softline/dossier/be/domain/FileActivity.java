package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.*;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE FileActivity SET deleted=true WHERE id=?")
@Where(clause = "deleted = false ")
public class FileActivity extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;

    @ManyToOne
    @JoinColumn
    Activity activity;

    @OneToMany(cascade = CascadeType.ALL ,mappedBy =ActivityDataField_.FILE_ACTIVITY,orphanRemoval = true)
    List<ActivityDataField> dataFields;

    @OneToMany(mappedBy = Reprise_.FILE_ACTIVITY)
    List<Reprise> reprises;

    @OneToMany(mappedBy = FileTask_.FILE_ACTIVITY)
    List<FileTask> fileTasks;

    @ManyToOne
    @JoinColumn
    File file;
    @OneToMany(mappedBy = Comment_.FILE_ACTIVITY)
    List<Comment> comments;
    boolean inTrash = false;

    @ManyToOne()
    @JoinColumn
    ActivityState state;
}
