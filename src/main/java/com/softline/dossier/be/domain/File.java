package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.*;

@SuperBuilder
@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
public class File extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String project;
    @Temporal(TemporalType.DATE)
    Date attributionDate;
    @Temporal(TemporalType.DATE)
    Date returnDeadline;
    @Temporal(TemporalType.DATE)
    Date provisionalDeliveryDate;
    @Temporal(TemporalType.DATE)
    Date deliveryDate;


    @ManyToOne
    @JoinColumn
    Client client;

    @OneToOne
    Commune commune;

    @OneToMany(mappedBy = FileDoc_.FILE)
    List<FileDoc> fileDocs;

    @OneToMany(cascade = CascadeType.ALL ,mappedBy = FileState_.FILE, fetch = FetchType.LAZY)
    List<FileState> fileStates;



    @OneToMany(cascade = CascadeType.ALL,mappedBy = FileActivity_.FILE, fetch = FetchType.LAZY)
    List<FileActivity> fileActivities;
    @OneToOne()
    @JoinColumn()
    Activity baseActivity;

    @Transient()
    FileState currentFileState;
    @Transient()
    FileActivity currentFileActivity;
}
