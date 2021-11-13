package com.softline.dossier.be.domain;

import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.FileTaskEvent;
import com.softline.dossier.be.security.domain.Agent;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE file_task SET deleted=true WHERE id=?")
@Where(clause = "deleted = false ")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class FileTask extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;
    @Type(type = "text")
    String title;

    @Column(name = "`order`")
    long order;// the order of this fileTask relative to it's FileActivity
    long number; // number of fileTasks relative to it's File
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "fileTask")
    @JoinColumn
    DescriptionComment description;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "fileTask")
    ReturnedComment retour;
    @ManyToOne(cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    FileActivity fileActivity;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    Task task;

    LocalDateTime toStartDate;
    LocalDateTime dueDate;
    LocalDateTime startDate;
    LocalDateTime endDate;
    @OneToOne
    @JoinColumn
    @NotFound(action = NotFoundAction.IGNORE)
    Agent reporter;
    @OneToOne
    @JoinColumn
    @NotFound(action = NotFoundAction.IGNORE)
    Agent assignedTo;
    @OneToMany(mappedBy = "fileTask", cascade = CascadeType.ALL)
    List<FileTaskSituation> fileTaskSituations;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    TaskState state;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    FileTask parent;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    List<FileTask> children;
    boolean returned;
    boolean inTrash;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    ReturnedCause returnedCause;

    @OneToMany(mappedBy = "fileTask", orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FileTaskAttachment> attachments = new ArrayList<>();

    public List<Attachment> getAttachments() {
        if (attachments == null) {
            return null;
        }
        return attachments.stream().map(e -> (Attachment) e).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "FileTask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    @NonNull
    public FileTaskSituation getCurrentState() {
        return getFileTaskSituations().stream().filter(FileTaskSituation::isCurrent).findFirst().get();
    }

    public void incrementOrder() {
        this.setOrder(this.getOrder() + 1);
    }

    public void decrementOrder() {
        this.setOrder(this.getOrder() - 1);
    }


    @Transient
    @Builder.Default
    private boolean wasTrashed = false;
    @Transient
    @Builder.Default
    private boolean wasRecovered = false;

    @PostPersist
    private void afterCreate() {
        new FileTaskEvent(EntityEvent.Type.ADDED, this).fireToAll();
    }

    @PostRemove
    private void afterDelete() {
        new FileTaskEvent(EntityEvent.Type.DELETED, this).fireToAll();
    }

    @PostUpdate
    private void afterUpdate() {
        if (wasTrashed) {
            new FileTaskEvent(EntityEvent.Type.TRASHED, this).fireToAll();
            wasTrashed = false;
        } else if (wasRecovered) {
            new FileTaskEvent(EntityEvent.Type.RECOVERED, this).fireToAll();
            wasRecovered = false;
        } else {
            new FileTaskEvent(EntityEvent.Type.UPDATED, this).fireToAll();
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
