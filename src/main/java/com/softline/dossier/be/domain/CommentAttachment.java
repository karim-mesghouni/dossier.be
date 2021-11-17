package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@SuperBuilder
@AllArgsConstructor
@Data

@NoArgsConstructor
@Entity
public class CommentAttachment extends Attachment {
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private Comment comment;
}