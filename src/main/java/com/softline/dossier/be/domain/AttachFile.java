package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@SuperBuilder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class AttachFile  extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    private String  path;
    private String name;
    private String url;
    @ManyToOne()
    @JoinColumn()
    FileTask fileTask;
}
