package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class CheckSheet extends Attachment {
    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private FileTask fileTask;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CheckItem> invalidItems;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FicheControl{" +
                "id=" + id +
                ", invalidItems=" + invalidItems +
                '}';
    }
}
