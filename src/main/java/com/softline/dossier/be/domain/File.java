package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
public class File extends BaseEntity {
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


    @ManyToOne
    @JoinColumn
    Client client;

    @OneToOne
    Commune commune;

    @OneToMany(mappedBy = FileDoc_.FILE, cascade = CascadeType.ALL)
    List<FileDoc> fileDocs;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = FileState_.FILE, fetch = FetchType.LAZY)
    List<FileState> fileStates;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = FileActivity_.FILE, fetch = FetchType.LAZY)
    List<FileActivity> fileActivities;
    @OneToOne()
    @JoinColumn()
    Activity baseActivity;
    @ManyToOne
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
