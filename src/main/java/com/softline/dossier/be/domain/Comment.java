package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.enums.CommentType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorColumn(name = "type",
        discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SQLDelete(sql = "UPDATE Comment SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class Comment extends BaseEntity implements IComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Type(type = "json")
    @Column(columnDefinition = "json")
    String content;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    FileActivity fileActivity;
    //TODO: need testing after removing the agent field
    @Enumerated(EnumType.STRING)
    @Column(insertable = false, updatable = false)
    CommentType type;
    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    FileTask fileTask;
    @OneToMany(mappedBy = "comment", orphanRemoval = true)
    List<Message> messages;

    @OneToMany(mappedBy = "comment", orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Builder.Default
    List<CommentAttachment> attachments = new ArrayList<>();
}
