package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE FileTaskSituation SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class FileTaskSituation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;
    @ManyToOne
    @JoinColumn
    TaskSituation situation;
    @OneToOne(mappedBy = Blocking_.STATE, cascade = CascadeType.ALL)
    Blocking blocking;
    @ManyToOne
    @JoinColumn()
    FileTask fileTask;

    @Override
    public String toString() {
        return "FileTaskSituation{" +
                "id=" + id +
                ", situation=" + situation +
                '}';
    }

    // for cloning without id
    public FileTaskSituation(@NotNull FileTaskSituation mirror) {
        id = 0;// get rid of ide warning
        setSituation(mirror.getSituation());
        setCurrent(mirror.isCurrent());
        setBlocking(mirror.getBlocking());
        setFileTask(mirror.getFileTask());
    }
}
