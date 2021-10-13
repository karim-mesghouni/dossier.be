package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.security.domain.Agent;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorColumn(name = Comment_.TYPE,
        discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SQLDelete(sql = "UPDATE Comment SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class Comment extends BaseEntity implements IComment
{

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
    @OneToMany(mappedBy = "comment", orphanRemoval = true)
    List<Message> messages;

    @OneToMany(mappedBy = "comment", orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Builder.Default
    List<CommentAttachment> attachments = new ArrayList<>();
}
