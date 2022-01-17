package com.softline.dossier.be.domain;

import com.softline.dossier.be.Tools.Functions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
public class CommentAttachment extends BaseEntity implements Attachment {
    @Transient
    Functions.UnsafeRunnable afterCreate;
    private String storageName;
    private String contentType;
    private String realName;


    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private Comment comment;

    @PostRemove
    public void afterRemove() {
        if (getPath().toFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            getPath().toFile().delete();
        }
    }

    @PostPersist
    public void afterCreating() {
        if (getAfterCreate() != null) {
            Functions.wrap(getAfterCreate());
        }
    }
}