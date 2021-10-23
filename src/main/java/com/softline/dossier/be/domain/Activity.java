package com.softline.dossier.be.domain;


import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Agent_;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
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

    @OneToMany(mappedBy = ActivityField_.ACTIVITY, cascade = CascadeType.ALL)
    @Builder.Default
    List<ActivityField> fields = new ArrayList<>();

    @OneToMany(mappedBy = FileActivity_.ACTIVITY)
    List<FileActivity> fileActivities;

    @OneToMany(mappedBy = Task_.ACTIVITY, cascade = CascadeType.ALL)
    List<Task> tasks;

    @OneToMany(mappedBy = ActivityState_.ACTIVITY, cascade = CascadeType.ALL)
    List<ActivityState> states;

    @OneToMany(mappedBy = Agent_.ACTIVITY, cascade = CascadeType.ALL)
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
