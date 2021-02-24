package com.softline.dossier.be.domain;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Type(type = "json")
    @Column(columnDefinition = "json")
    String content;
    @ManyToOne
    @JoinColumn
    File file;
    @ManyToOne
    @JoinColumn
    Agent agent;
}
