package com.softline.task.manger.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        long id;


        String name;
        String description;

        @OneToMany(cascade = CascadeType.ALL,mappedBy = "activity")
        List<ActivityField> fields;

        @OneToMany(mappedBy = "activity")
        List<FileActivity> fileActivities;

}
