package com.softline.dossier.be.security.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @OneToMany(mappedBy = Agent_.ROLE, cascade = CascadeType.ALL)
    List<Agent> agents;
    private String displayName;
}
