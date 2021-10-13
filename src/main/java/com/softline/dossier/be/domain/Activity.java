package com.softline.dossier.be.domain;


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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = ActivityField_.ACTIVITY)
    @Builder.Default
    List<ActivityField> fields = new ArrayList<>();

    @OneToMany(mappedBy = FileActivity_.ACTIVITY)
    List<FileActivity> fileActivities;

    @OneToMany(mappedBy = Task_.ACTIVITY, cascade = CascadeType.ALL)
    List<Task> tasks;

    @OneToMany(mappedBy = ActivityState_.ACTIVITY, cascade = CascadeType.ALL)
    List<ActivityState> states;

    @Override
    public String toString()
    {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
