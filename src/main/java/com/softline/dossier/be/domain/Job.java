package com.softline.dossier.be.domain;

import com.softline.dossier.be.security.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE job SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class Job extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String name;

    @OneToMany(mappedBy = "job")
    @NotFound(action = NotFoundAction.IGNORE)
    List<Agent> agents;
}
