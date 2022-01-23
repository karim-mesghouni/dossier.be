package com.softline.dossier.be.domain;

import com.softline.dossier.be.Tools.ListUtils;
import com.softline.dossier.be.domain.traits.HasOrder;
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

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE file_task SET deleted=true WHERE id=?")
@Where(clause = "deleted = false ")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class FileTask extends BaseEntity implements HasOrder {
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
    @OneToOne(mappedBy = "fileTask", orphanRemoval = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private ControlSheet controlSheet;

    @Override
    public String toString() {
        return "FileTask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    @NonNull
    public FileTaskSituation getCurrentFileTaskSituation() throws RuntimeException {
        return ListUtils.filterFirst(getFileTaskSituations(), FileTaskSituation::isCurrent)
                .orElseThrow(() -> new RuntimeException("FileTask " + getId() + " has no active situation"));
    }

    public void incrementOrder() {
        this.setOrder(this.getOrder() + 1);
    }

    public void decrementOrder() {
        this.setOrder(this.getOrder() - 1);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
