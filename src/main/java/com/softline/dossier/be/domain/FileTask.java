package com.softline.dossier.be.domain;

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
    long order;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "fileTask")
    @JoinColumn
    DescriptionComment description;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "fileTask")
    ReturnedComment retour;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    FileActivity fileActivity;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    Task task;

    LocalDateTime toStartDate;
    LocalDateTime dueDate;
    LocalDateTime startDate;
    LocalDateTime endDate;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @NotFound(action = NotFoundAction.IGNORE)
    Agent reporter;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @NotFound(action = NotFoundAction.IGNORE)
    Agent assignedTo;
    @OneToMany(mappedBy = "fileTask", cascade = CascadeType.ALL)
    List<FileTaskSituation> fileTaskSituations;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    TaskState state;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    FileTask parent;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    List<FileTask> children;
    boolean returned;
    boolean inTrash;
    int fileTaskOrder;
    @ManyToOne(fetch = FetchType.LAZY)
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
}
