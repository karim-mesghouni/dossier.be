package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class CheckItem implements HasId {
    private String groupName;
    private String text;
    private String description;
    @Id
    private long id;
    @ManyToOne
    @ToString.Exclude
    private CheckSheet checkSheet;

    public CheckItem(String groupName, String text, String description) {
        this.groupName = groupName;
        this.text = text;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CheckItem checkItem = (CheckItem) o;
        return Objects.equals(id, checkItem.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
