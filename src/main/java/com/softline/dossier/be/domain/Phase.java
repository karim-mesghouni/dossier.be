package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class Phase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String description;
    @ManyToOne
    @JoinColumn
    Activity activity;

    @OneToMany(mappedBy = Job_.PHASE,cascade = CascadeType.ALL)
    List<Job> jobs;
    @OneToMany(mappedBy = PhaseState_.PHASE,cascade = CascadeType.ALL)
    List<PhaseState> states;

    @OneToMany(mappedBy = FilePhase_.PHASE)
    List<FilePhase> filePhases;
}
