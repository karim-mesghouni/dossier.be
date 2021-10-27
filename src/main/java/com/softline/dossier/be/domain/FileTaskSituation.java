package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE file_task_situation SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class FileTaskSituation extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    TaskSituation situation;
    @OneToOne(mappedBy = "state", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Blocking blocking;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn()
    FileTask fileTask;

    @Override
    public String toString()
    {
        return "FileTaskSituation{" +
                "id=" + id +
                ", situation=" + situation +
                '}';
    }
}
