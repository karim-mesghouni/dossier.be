package com.softline.task.manger.domain;

import com.softline.task.manger.domain.enums.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityDataField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String fieldName;

    @Enumerated(EnumType.STRING)
    FieldType fieldType;
    String data;

    @ManyToOne
    @JoinColumn(name = "fileActivity_id")
    FileActivity fileActivity;
}
