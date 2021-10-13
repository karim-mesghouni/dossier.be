package com.softline.dossier.be.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE Client SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class Client extends BaseEntity
{
    @OneToMany(mappedBy = File_.CLIENT)
    List<File> files;
    @OneToMany(mappedBy = VisAVis_.CLIENT)
    List<VisAVis> visAVis;
    @Cascade(CascadeType.ALL)
    @OneToMany(mappedBy = "client")
    @Builder.Default
    List<Contact> contacts = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String address;

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
    public String toString()
    {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
