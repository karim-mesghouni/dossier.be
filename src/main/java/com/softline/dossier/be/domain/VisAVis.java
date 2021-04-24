package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE VisAVis SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class VisAVis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String email;
    String phone;
    @ManyToOne
    @JoinColumn
    Client client;
    @ManyToOne
    @JoinColumn
    BlockingLockingAddress lockingAddress;
}
