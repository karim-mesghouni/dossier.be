package com.softline.dossier.be.domain;

import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.FileEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@AllArgsConstructor
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SQLDelete(sql = "UPDATE File SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class File extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "`order`")
    long order;
    String project;
    LocalDate attributionDate;
    LocalDate returnDeadline;
    LocalDate provisionalDeliveryDate;
    LocalDate deliveryDate;

    // used to keep track of fileTasks in this file, it will always increment when we add a new fileTask
    // also useful in the case where the file has fileTasks before but they was removed,
    // so this is a replacement for using the count method on fileTasks
    @Column(columnDefinition = "integer default 1")
    long nextFileTaskNumber;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    Client client;

    @OneToOne(fetch = FetchType.LAZY)
    Commune commune;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<FileState> fileStates;


    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<FileActivity> fileActivities;
    @OneToOne(fetch = FetchType.LAZY)
    Activity baseActivity;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    File reprise;
    boolean inTrash;
    @Transient()
    FileState currentFileState;
    @Transient()
    FileActivity currentFileActivity;


    List<Document> getDocuments() {
        return getFileActivities().stream().map(FileActivity::getDocuments).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public void incrementOrder() {
        this.setOrder(this.getOrder() + 1);
    }

    public void decrementOrder() {
        this.setOrder(this.getOrder() - 1);
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", project='" + project + '\'' +
                '}';
    }

    @Transient
    @Builder.Default
    private boolean wasTrashed = false;


    public void incrementNextFileTaskNumber() {
        setNextFileTaskNumber(1 + getNextFileTaskNumber());
    }

    @Transient
    @Builder.Default
    private boolean wasRecovered = false;

    // used by graphql File type (field fileReprise)
    @SuppressWarnings("unused")
    public boolean isFileReprise() {
        return getReprise() != null;
    }

    @PostPersist
    private void afterCreate() {
        new FileEvent(EntityEvent.Type.ADDED, this).fireToAll();
    }

    @PostRemove
    private void afterDelete() {
        new FileEvent(EntityEvent.Type.DELETED, this).fireToAll();
    }

    @PostUpdate
    private void afterUpdate() {
        if (wasTrashed) {
            new FileEvent(EntityEvent.Type.TRASHED, this).fireToAll();
            wasTrashed = false;
        } else if (wasRecovered) {
            new FileEvent(EntityEvent.Type.RECOVERED, this).fireToAll();
            wasRecovered = false;
        } else {
            new FileEvent(EntityEvent.Type.UPDATED, this).fireToAll();
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
