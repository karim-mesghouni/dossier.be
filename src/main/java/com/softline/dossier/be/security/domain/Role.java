package com.softline.dossier.be.security.domain;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator;
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
    @Column(name = "name", nullable = false)
    private Type type;

    @OneToMany(mappedBy = "role")
    List<Agent> agents;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + type + '\'' +
                '}';
    }

    private String displayName;

    public String getName() {
        return getType().toString();
    }

    /**
     * @return true if this role represents an admin role
     */
    public boolean isAdmin() {
        return getName().equals("MANAGER");
    }

    public boolean is(Type type) {
        return getType().equals(type);
    }

    /**
     * if you will modify these role names make sure that {@link Agent#isAdmin()} is valid<
     * and check all defined json policies, and all calls to @PreAuthorize and {@link AttributeBasedAccessControlEvaluator#DenyOrProceed(String, Object)}
     */
    public enum Type {
        MANAGER,
        REFERENT,
        VALIDATOR,
        ACCOUNTANT
    }
}
