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
import java.time.LocalDate;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SQLDelete(sql = "UPDATE File SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class File extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "`order`")
    long order;
    String project;
    LocalDate attributionDate;
    LocalDate returnDeadline;
    LocalDate provisionalDeliveryDate;
    LocalDate deliveryDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    Client client;

    @OneToOne(fetch = FetchType.LAZY)
    Commune commune;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    List<FileDoc> fileDocs;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<FileState> fileStates;


    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<FileActivity> fileActivities;
    @OneToOne(fetch = FetchType.LAZY)
    Activity baseActivity;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    File reprise;
    boolean fileReprise;
    boolean inTrash;
    @Transient()
    FileState currentFileState;
    @Transient()
    FileActivity currentFileActivity;


    public void incrementOrder()
    {
        this.setOrder(this.getOrder() + 1);
    }

    public void decrementOrder()
    {
        this.setOrder(this.getOrder() - 1);
    }

    @Override
    public String toString()
    {
        return "File{" +
                "id=" + id +
                ", project='" + project + '\'' +
                '}';
    }
}
