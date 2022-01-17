package com.softline.dossier.be.domain;

import com.softline.dossier.be.Tools.Functions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE control_sheet SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class ControlSheet extends BaseEntity implements Attachment {
    @Transient
    Functions.UnsafeRunnable afterCreate;
    private String storageName;
    private String contentType;
    private String realName;
    @OneToMany(mappedBy = "controlSheet")
    private List<CheckItem> invalidItems;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private FileTask fileTask;

    public ControlSheet(FileTask fileTask, List<CheckItem> invalidItems) {
        this.fileTask = fileTask;
        this.invalidItems = invalidItems;
    }

    // used by graphql
    @SuppressWarnings("unused")
    public boolean isValid() {
        return invalidItems.isEmpty();
    }

    @Override
    public String toString() {
        return "FicheControl{" +
                "id=" + id +
                ", invalidItems=" + invalidItems +
                '}';
    }

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
