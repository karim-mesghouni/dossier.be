package com.softline.dossier.be.domain;


import com.softline.dossier.be.security.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class Activity extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;


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
    public String toString()
    {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
