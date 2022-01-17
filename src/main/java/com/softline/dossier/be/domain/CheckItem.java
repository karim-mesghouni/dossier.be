package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    private ControlSheet controlSheet;

    public CheckItem(ControlSheet controlSheet, String groupName, String text, String description) {
        this.groupName = groupName;
        this.text = text;
        this.description = description;
        this.controlSheet = controlSheet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckItem checkItem = (CheckItem) o;
        return id != null && Objects.equals(checkItem.id, id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
