package com.softline.dossier.be.domain;

import com.softline.dossier.be.security.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@SuperBuilder
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE file_doc  SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class FileDoc extends BaseEntity {
    @ManyToOne
    @JoinColumn
    File file;
    @ManyToOne
    @JoinColumn
    FileActivity fileActivity;
    @ManyToOne
    @JoinColumn
    Agent agent;
    String description;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String path;
}
