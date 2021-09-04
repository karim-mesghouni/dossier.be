package com.softline.dossier.be.domain;

import com.softline.dossier.be.graphql.types.input.ClientInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.*;

@Data
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE Client SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class Client extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String address;
    @OneToMany(mappedBy = File_.CLIENT)
    List<File> files;
    @OneToMany(mappedBy = VisAVis_.CLIENT)
    List<VisAVis> visAVis;

    @OneToMany(mappedBy = "client")
    List<Contact> contacts = new ArrayList<>();
}
