package com.softline.task.manger.domain;


import com.softline.task.manger.domain.enums.FieldType;
import lombok.*;

import javax.persistence.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ActivityField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String fieldName;

    @Enumerated(EnumType.STRING)
    FieldType  fieldType;


    @ManyToOne
    @JoinColumn(name = "activity_id")
    Activity activity;
}
