package com.softline.dossier.be.domain;

import com.softline.dossier.be.Tools.Functions;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class FileTaskAttachment extends BaseEntity implements Attachment {
    @Transient
    Functions.UnsafeRunnable afterCreate;
    private String storageName;
    private String contentType;
    private String realName;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @ToString.Exclude
    private FileTask fileTask;


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
