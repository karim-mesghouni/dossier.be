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
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@Entity
@Data
@NoArgsConstructor
@SQLDelete(sql = "UPDATE File SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class File extends BaseEntity implements HasOrder {
    @Column(name = "`order`")
    long order;
    String project;
    /**
     * Date d'attribution
     */
    LocalDate attributionDate;
    /**
     * Date limite de retour
     */
    LocalDate returnDeadline;
    /**
     * Date prévisionnel de livraison
     */
    LocalDate provisionalDeliveryDate;
    /**
     * Date de Livraison
     */
    LocalDate deliveryDate;

    // used to keep track of fileTasks in this file, it will always increment when we add a new fileTask
    // also useful in the case where the file has fileTasks before, but they were removed,
    // so this is a replacement for using the count method on fileTaskRepository which won't count deleted fileTasks
    @Column(columnDefinition = "integer default 1")
    @Builder.Default
    long nextFileTaskNumber = 1;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    Client client;

    @OneToOne(fetch = FetchType.LAZY)
    Commune commune;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    List<FileState> fileStates = new ArrayList<>();

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    List<FileActivity> fileActivities = new ArrayList<>();
    @OneToOne(fetch = FetchType.LAZY)
    Activity baseActivity;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    File reprise;
    boolean inTrash;
    @Transient()
    FileState currentFileState;
    @Transient()
    FileActivity currentFileActivity;

    public void incrementOrder() {
        this.setOrder(this.getOrder() + 1);
    }

    public void decrementOrder() {
        this.setOrder(this.getOrder() - 1);
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", project='" + project + '\'' +
                '}';
    }

    public void incrementNextFileTaskNumber() {
        setNextFileTaskNumber(1 + getNextFileTaskNumber());
    }

    // used by graphql File type (field fileReprise)
    @SuppressWarnings("unused")
    public boolean isFileReprise() {
        return getReprise() != null;
    }
}
