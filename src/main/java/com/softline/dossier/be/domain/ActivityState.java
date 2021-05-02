package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.*;
@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE ActivityState SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class ActivityState extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;

    boolean initial;
    boolean Final;

    @ManyToOne
    @JoinColumn
    Activity activity;

    @OneToMany(cascade = CascadeType.ALL ,mappedBy =FileActivity_.STATE ,orphanRemoval = true)
    List<FileActivity> fileActivities;
}

