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
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;
    @ManyToMany(mappedBy = "privileges")
    private List<Role> roles;
}
