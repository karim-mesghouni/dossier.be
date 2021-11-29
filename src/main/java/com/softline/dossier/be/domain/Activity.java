package com.softline.dossier.be.domain;


import com.softline.dossier.be.security.domain.Agent;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuperBuilder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class Activity extends BaseEntity {
    String name;
    String description;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    @Builder.Default
    List<ActivityField> fields = new ArrayList<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    List<FileActivity> fileActivities;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    List<Task> tasks;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    List<ActivityState> states;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    List<Agent> agents;

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Activity activity = (Activity) o;
        return Objects.equals(id, activity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
