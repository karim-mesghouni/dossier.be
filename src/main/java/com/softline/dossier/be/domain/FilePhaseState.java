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
    @ManyToOne
    @JoinColumn
    FilePhaseAgent phaseAgent;
    @ManyToOne
    @JoinColumn
    PhaseState state;
    @OneToMany(mappedBy = Blocking_.STATE)
    List<Blocking> blockings;
}
