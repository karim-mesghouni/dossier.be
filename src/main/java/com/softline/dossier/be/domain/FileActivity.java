package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
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
    List<ActivityDataField> dataFields;

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
}
