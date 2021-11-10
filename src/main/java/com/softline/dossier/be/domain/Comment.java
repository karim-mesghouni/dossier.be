package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.events.CommentEvent;
import com.softline.dossier.be.events.types.EntityEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.softline.dossier.be.Halpers.TipTap.resolveCommentContent;

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
@Slf4j(topic = "CommentEntity")
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
    @OneToMany(mappedBy = "comment", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    @NotNull
    List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "comment", orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Builder.Default
    List<CommentAttachment> attachments = new ArrayList<>();

    @Transient
    @Builder.Default
    boolean needsResolving = true;


    @PostUpdate
    private void afterUpdate() {
        new CommentEvent(EntityEvent.Type.UPDATED, this).fireToAll();
    }

    @PostPersist
    private void afterCreate() {
        new CommentEvent(EntityEvent.Type.ADDED, this).fireToAll();
    }

    @PostRemove
    private void afterRemove() {
        new CommentEvent(EntityEvent.Type.DELETED, this).fireToAll();
    }

    /**
     * after we save the comment we will parse the json content,
     * extract any foreign image links and save them locally,
     * then find any mentions and add them to the message list of the comment
     *
     * @see Message#sendEvent()
     */
    @PreUpdate
    @PrePersist
    private void resolveContent() {
        if (needsResolving) {
            needsResolving = false;
            resolveCommentContent(this);
        }
    }

    public void setContent(String content) {
        this.content = content;
        needsResolving = true;// marks it for resolving for the next persist or update call
    }

    @Override
    public String toString() {
        return "Comment{" +
                "agent=" + agent +
                ", id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
