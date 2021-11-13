package com.softline.dossier.be.domain;

import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.FileActivityEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE file_activity SET deleted=true WHERE id=?")
@Where(clause = "deleted = false ")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class FileActivity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    Activity activity;

    @OneToMany(mappedBy = "fileActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<ActivityDataField> dataFields = new ArrayList<>();

    @OneToMany(mappedBy = "fileActivity")
    List<Reprise> reprises;

    @OneToMany(mappedBy = "fileActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<FileTask> fileTasks;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    File file;
    @OneToMany(mappedBy = "fileActivity")
    List<Comment> comments;
    boolean inTrash;
    @Column(name = "`order`")
    long order;

    @ManyToOne()
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    ActivityState state;


    public void incrementOrder() {
        this.setOrder(this.getOrder() + 1);
    }

    public void decrementOrder() {
        this.setOrder(this.getOrder() - 1);
    }

    @Override
    public String toString() {
        return "FileActivity{" +
                "id=" + id +
                ", activity=" + activity +
                '}';
    }

    @Transient
    @Builder.Default
    private boolean wasTrashed = false;
    @Transient
    @Builder.Default
    private boolean wasRecovered = false;

    @PostPersist
    private void afterCreate() {
        new FileActivityEvent(EntityEvent.Type.ADDED, this).fireToAll();
    }

    @PostRemove
    private void afterDelete() {
        new FileActivityEvent(EntityEvent.Type.DELETED, this).fireToAll();
    }

    @PostUpdate
    private void afterUpdate() {
        if (wasTrashed) {
            new FileActivityEvent(EntityEvent.Type.TRASHED, this).fireToAll();
            wasTrashed = false;
        } else if (wasRecovered) {
            new FileActivityEvent(EntityEvent.Type.RECOVERED, this).fireToAll();
            wasRecovered = false;
        } else {
            new FileActivityEvent(EntityEvent.Type.UPDATED, this).fireToAll();
        }
    }

    public void setInTrash(boolean inTrash) {
        if (getId() != 0) {
            if (inTrash) {
                wasTrashed = true;
            } else {
                wasRecovered = true;
            }
        }
        this.inTrash = inTrash;
    }
}
