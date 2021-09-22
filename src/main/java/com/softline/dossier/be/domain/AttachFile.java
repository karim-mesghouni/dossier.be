package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class AttachFile extends BaseEntity {
    @ManyToOne()
    @JoinColumn()
    FileTask fileTask;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String path;
    private String name;
    private String url;
}
