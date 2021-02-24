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

    Date attributionDate;

    Date returnDeadline;

    Date provisionalDeliveryDate;

    Date deliveryDate;

    String cem;

    @ManyToOne
    @JoinColumn
    Client client;
    @OneToOne
    Commune commune;

    @OneToMany(mappedBy = FileDoc_.FILE)
    List<FileDoc> fileDocs;

    @OneToMany(mappedBy = FileState_.FILE, fetch = FetchType.LAZY)
    List<FileState> fileStates;
    @OneToMany(mappedBy = Comment_.FILE)
    List<Comment> comments;

}
