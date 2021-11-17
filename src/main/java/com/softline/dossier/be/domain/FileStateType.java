package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@SQLDelete(sql = "UPDATE file_state_type SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class FileStateType extends BaseEntity {
    String state;
    boolean initial;
    boolean Final;
    @Override
    public String toString() {
        return "FileStateType{" +
                "id=" + id +
                ", state='" + state + '\'' +
                '}';
    }
}
