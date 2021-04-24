package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.security.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import java.util.*;
import javax.persistence.*;

@Entity
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorColumn(name=Comment_.TYPE,
        discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SQLDelete(sql = "UPDATE Comment SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class Comment extends BaseEntity implements  IComment{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Type(type = "json")
    @Column(columnDefinition = "json")
    String content;
    @ManyToOne
    @JoinColumn
    FileActivity fileActivity;
    @ManyToOne
    @JoinColumn
    Agent agent;
    @Enumerated(EnumType.STRING)
    @Column(insertable = false, updatable = false)
    CommentType type;
    @OneToOne
    @JoinColumn
    FileTask fileTask;
    @OneToMany(mappedBy = "comment")
    List<Message> messages;

}
