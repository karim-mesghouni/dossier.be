package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.*;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FilePhaseAgent extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Temporal(TemporalType.DATE)
    Date toStartDate;
    @Temporal(TemporalType.DATE)
    Date dueDate;
    @Temporal(TemporalType.DATE)
    Date startDate;
    @Temporal(TemporalType.DATE)
    Date endDate;
    boolean current;

    @ManyToOne
    @JoinColumn
    FilePhase filePhase;
    @OneToOne
    Agent reporter;
    @OneToOne
    Agent assignedTo;

    @OneToMany(mappedBy = FilePhaseState_.PHASE_AGENT,cascade = CascadeType.ALL)
    List<FilePhaseState> filePhaseStates;
}
