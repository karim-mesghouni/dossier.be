package com.softline.dossier.be.domain;

import com.softline.dossier.be.security.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE FileTask SET deleted=true WHERE id=?")
@Where(clause = "deleted = false ")
public class FileTask extends BaseEntity {
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
    @Transient()
    FileTaskSituation currentFileTaskSituation;
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
    private List<FileTaskAttachment> attachments;

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
}
