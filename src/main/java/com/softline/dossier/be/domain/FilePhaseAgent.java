package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FilePhaseAgent extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    Date toStartDate;
    Date dueDate;
    Date startDate;
    Date endDate;
    boolean current;

    @ManyToOne
    @JoinColumn
    FilePhase filePhase;
    @OneToOne
    Agent reporter;
    @OneToOne
    Agent assignedTo;
    @ManyToOne
    @JoinColumn
    FilePhaseState filePhaseState;
}
