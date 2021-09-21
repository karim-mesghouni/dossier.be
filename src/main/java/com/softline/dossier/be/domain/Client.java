package com.softline.dossier.be.domain;

import com.softline.dossier.be.graphql.types.input.ClientInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.*;
import java.util.function.Predicate;

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

    @Cascade(CascadeType.ALL)
    @OneToMany(mappedBy = "client")
    List<Contact> contacts = new ArrayList<>();



    public void addContact(Contact c)
    {
        this.contacts.add(c);
    }
    public Contact findInContacts(Predicate<Contact> search)
    {
        return this.contacts.stream()
                .filter(search)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
