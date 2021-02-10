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
public class FilePhaseState  extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;
    @OneToMany(mappedBy = FilePhaseAgent_.FILE_PHASE_STATE)
    List<FilePhaseAgent> phaseAgents;
    @ManyToOne
    @JoinColumn
    PhaseState state;
    @OneToMany(mappedBy = Blocking_.STATE)
    List<Blocking> blockings;
}
