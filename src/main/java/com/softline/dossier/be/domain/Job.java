package com.softline.dossier.be.domain;

import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Agent_;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
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
public class Job extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String name;

    @OneToMany(mappedBy = Agent_.JOB, cascade = CascadeType.ALL)
    List<Agent> agents;
}
