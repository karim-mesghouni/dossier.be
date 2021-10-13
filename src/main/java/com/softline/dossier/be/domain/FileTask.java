package com.softline.dossier.be.domain;

import com.softline.dossier.be.security.domain.Agent;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
public class FileTask extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;
    @Type(type = "text")
    String title;
    Long number;
    @OneToOne
    @JoinColumn
    DescriptionComment description;
    @OneToOne
    @JoinColumn
    ReturnedComment retour;
    @ManyToOne
    @JoinColumn
    FileActivity fileActivity;
    @ManyToOne
    @JoinColumn
    Task task;

    LocalDateTime toStartDate;
    LocalDateTime dueDate;
    LocalDateTime startDate;
    LocalDateTime endDate;
    @OneToOne
    Agent reporter;
    @OneToOne
    Agent assignedTo;
    @OneToMany(mappedBy = FileTaskSituation_.FILE_TASK, cascade = CascadeType.ALL)
    List<FileTaskSituation> fileTaskSituations;
    @ManyToOne
    @JoinColumn
    TaskState state;
    @OneToOne
    @JoinColumn
    FileTask parent;
    boolean returned;
    boolean inTrash;
    int fileTaskOrder;
    @ManyToOne
    @JoinColumn
    ReturnedCause returnedCause;

    @OneToMany(mappedBy = "fileTask", orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FileTaskAttachment> attachments = new ArrayList<>();

    public List<Attachment> getAttachments()
    {
        if (attachments == null) {
            return null;
        }
        return attachments.stream().map(e -> (Attachment) e).collect(Collectors.toList());
    }

    @Override
    public String toString()
    {
        return "FileTask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    @NonNull
    public FileTaskSituation getCurrentState()
    {
        return getFileTaskSituations().stream().filter(FileTaskSituation::isCurrent).findFirst().get();
    }
}
