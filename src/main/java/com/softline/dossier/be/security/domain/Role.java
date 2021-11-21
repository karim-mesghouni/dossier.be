package com.softline.dossier.be.security.domain;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role implements HasId {
    @OneToMany(mappedBy = "role")
    List<Agent> agents;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    private String displayName;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * @return true if this role represents an admin role
     */
    public boolean isAdmin() {
        return getName().equals("MANAGER");
    }
}
