package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE FileTaskSituation SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class FileTaskSituation extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean current;
    @ManyToOne
    @JoinColumn
    TaskSituation situation;
    @OneToMany(mappedBy = Blocking_.STATE)
    List<Blocking> blockings;
    @ManyToOne
    @JoinColumn()
    FileTask fileTask;
}
