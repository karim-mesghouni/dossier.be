package com.softline.dossier.be.domain;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

//region annotations
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE Contact SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
//endregion
public class Contact
{
    @Id
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Boolean deleted;


    //region relations
    @ManyToOne
    private Client client;
    //endregion

    //region auto-generated
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Contact contact = (Contact) o;

        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode()
    {
        return 590563367;
    }
    //endregion
}
