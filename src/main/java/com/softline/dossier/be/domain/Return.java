package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@SuperBuilder
@Entity(name = "tb_return")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE Return SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class Return extends BaseEntity {
    int number;
    String cause;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    FileTask fileTask;

    @Override
    public String toString() {
        return "Return{" +
                "number=" + number +
                ", cause='" + cause + '\'' +
                '}';
    }
}
