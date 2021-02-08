package com.softline.task.manger.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FileActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    Activity activity;
    @OneToMany(cascade = CascadeType.ALL ,mappedBy ="fileActivity",orphanRemoval = true,fetch = FetchType.EAGER)
    List<ActivityDataField> dataFields;
}
