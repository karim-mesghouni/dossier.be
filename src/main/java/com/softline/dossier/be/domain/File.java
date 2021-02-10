package com.softline.dossier.be.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.*;
@SuperBuilder

@Entity
@Data
@NoArgsConstructor
public class File extends BaseEntity {
    @Id
    private long id;

    String project;

    Date attributionDate;

    Date returnDeadline;

    Date provisionalDeliveryDate;

    Date deliveryDate;

    String CEM;

    @ManyToOne
    @JoinColumn
    Client client;
    @OneToOne
    Commune commune;

    @OneToMany(mappedBy = FileDoc_.FILE)
    List<FileDoc> fileDoc;


}
