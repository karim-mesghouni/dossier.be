package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE file_task_situation SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")

public class FileTaskSituation extends BaseEntity {
    boolean current;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    TaskSituation situation;
    @OneToOne(mappedBy = "state", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Blocking blocking;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn()
    FileTask fileTask;

    @Override
    public String toString() {
        return "FileTaskSituation{" +
                "id=" + id +
                ", situation=" + situation +
                '}';
    }
}
