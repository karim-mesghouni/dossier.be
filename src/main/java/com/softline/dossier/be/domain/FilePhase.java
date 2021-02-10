package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.*;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FilePhase extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;

    @ManyToOne
    @JoinColumn
    FileActivity fileActivity;

    @OneToMany( mappedBy = Return_.FILE_PHASE)
    List<Return> Return;
    @OneToMany( mappedBy = FilePhaseAgent_.FILE_PHASE)

    List<FilePhaseAgent> filePhaseAgents;
}
