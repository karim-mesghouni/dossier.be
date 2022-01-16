package com.softline.dossier.be.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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
    public int hashCode() {
        return getClass().hashCode();
    }
}
