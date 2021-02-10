package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.*;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("1")
public class FileActivity extends  FileState{


    @ManyToOne
    @JoinColumn
    Activity activity;
    @OneToMany(cascade = CascadeType.ALL ,mappedBy =ActivityDataField_.FILE_ACTIVITY,orphanRemoval = true)
    List<ActivityDataField> dataFields;

    @OneToMany(mappedBy = Reprise_.FILE_ACTIVITY)
    List<Reprise> repises;
    
    @OneToMany(mappedBy = FilePhase_.FILE_ACTIVITY)
    List<FilePhase> filePhases;
}
