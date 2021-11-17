package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@SuperBuilder
@Data

@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE file_state SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class FileState extends BaseEntity {
    boolean current;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    FileStateType type;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    File file;

    @Override
    public String toString() {
        return "FileState{" +
                "id=" + id +
                ", type=" + type +
                '}';
    }
}
