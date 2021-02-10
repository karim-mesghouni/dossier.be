package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Date;

@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER)
public class FileState extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id ;

    boolean current;

    @ManyToOne
    @JoinColumn
    FileStateType type;
}
