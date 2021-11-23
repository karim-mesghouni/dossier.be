package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.traits.HasOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@NamedQuery(name = "FileActivity.getAllByFileId", query = "SELECT fa FROM FileActivity fa WHERE fa.file.id = :fileId AND fa.inTrash = false ORDER BY fa.order")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE file_activity SET deleted=true WHERE id=?")
@Where(clause = "deleted = false ")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class FileActivity extends BaseEntity implements HasOrder {
    boolean current;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    Activity activity;

    @OneToMany(mappedBy = "fileActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<ActivityDataField> dataFields = new ArrayList<>();

    @OneToMany(mappedBy = "fileActivity")
    List<Reprise> reprises;

    @OneToMany(mappedBy = "fileActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<FileTask> fileTasks = new ArrayList<>();


    @OneToMany(mappedBy = "fileActivity", cascade = CascadeType.ALL)
    @Builder.Default
    List<Document> documents = new ArrayList<>();

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    File file;
    @OneToMany(mappedBy = "fileActivity")
    @Builder.Default
    List<Comment> comments = new ArrayList<>();
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
