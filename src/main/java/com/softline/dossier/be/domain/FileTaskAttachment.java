package com.softline.dossier.be.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Objects;

@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class FileTaskAttachment extends Attachment {
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @ToString.Exclude
    private FileTask fileTask;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FileTaskAttachment that = (FileTaskAttachment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
