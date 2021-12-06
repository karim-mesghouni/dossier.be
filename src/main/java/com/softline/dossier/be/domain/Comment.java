package com.softline.dossier.be.domain;

import com.softline.dossier.be.Tools.EnvUtil;
import com.softline.dossier.be.domain.enums.CommentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@Data

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
public class Comment extends BaseEntity {
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

    @Override
    public String toString() {
        return "Comment{" +
                "agent=" + agent +
                ", id=" + id +
                ", content='" + content + '\'' +
                '}';
    }

    public String getContent() {
        return content.replaceAll("http@\\$%!SERVER_URL!%\\$@", EnvUtil.getServerUrl());
    }
}
