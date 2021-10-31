package com.softline.dossier.be.domain;

import com.softline.dossier.be.security.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@SuperBuilder
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE Message SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class Message extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    boolean readMessage;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    Comment comment;
    @ManyToOne
    @JoinColumn
    @NotFound(action = NotFoundAction.IGNORE)
    Agent agent;
}
