package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Entity(name = "tb_return")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Return extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    int number;
    String cause;
    @ManyToOne
    @JoinColumn
    FileTask fileTask;

}
