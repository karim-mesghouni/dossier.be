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
public class PhaseState extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String state;
    boolean initial;
    boolean Final;
    @ManyToOne()
    @JoinColumn()
    Phase phase;
    @OneToMany(mappedBy = FilePhaseState_.STATE)
    List<FilePhaseState> filePhaseStates;
}
