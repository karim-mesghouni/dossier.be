package com.softline.dossier.be.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Objects;

//region annotations
@SuperBuilder
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
    @GeneratedValue(
            strategy= GenerationType.AUTO,
            generator="native"
    )
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    private long id;
    private String name;
    private String phone;
    private String email;
    private boolean deleted = Boolean.FALSE;


    //region relations
    @ManyToOne
    @JoinColumn(name = "client_id")
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
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode()
    {
        return 590563367;
    }
    //endregion
}
