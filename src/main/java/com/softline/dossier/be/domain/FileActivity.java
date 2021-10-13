package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.annotations.Where;

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
public class FileActivity extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;

    @ManyToOne
    @JoinColumn
    Activity activity;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = ActivityDataField_.FILE_ACTIVITY, orphanRemoval = true)
    List<ActivityDataField> dataFields;

    @OneToMany(mappedBy = Reprise_.FILE_ACTIVITY)
    List<Reprise> reprises;

    @OneToMany(mappedBy = FileTask_.FILE_ACTIVITY, cascade = CascadeType.ALL)
    List<FileTask> fileTasks;

    @ManyToOne
    File file;
    @OneToMany(mappedBy = Comment_.FILE_ACTIVITY)
    List<Comment> comments;
    boolean inTrash;
    @Column(columnDefinition = "int default 1")
    int fileActivityOrder;

    @ManyToOne()
    @JoinColumn
    ActivityState state;

    @Override
    public String toString()
    {
        return "FileActivity{" +
                "id=" + id +
                ", activity=" + activity +
                '}';
    }
}
