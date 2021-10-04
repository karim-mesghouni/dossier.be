package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE file_state SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class FileState extends BaseEntity {
    boolean current;
    @ManyToOne
    @JoinColumn
    FileStateType type;
    @ManyToOne
    File file;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Override
    public String toString() {
        return "FileState{" +
                "id=" + id +
                ", type=" + type +
                '}';
    }
}
