package com.softline.dossier.be.security.domain;

import com.softline.dossier.be.Application;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.security.repository.AgentRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.annotations.Where;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE agent SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class Agent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;

    @OneToMany(mappedBy = "agent", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    List<Comment> comments;
    @OneToMany(mappedBy = "agent")
    List<Notification> notifications;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    private String email;
    private boolean enabled;
    private boolean tokenExpired;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;

    @OneToMany(mappedBy = "agent")
    private List<File> createdFiles;

    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", activity=" + activity +
                '}';
    }

    public boolean isAdmin() {
        return getRole() != null && Objects.equals(getRole().getName(), "MANAGER");
    }

    /**
     * return the current request agent, the agent returned is detached and might not have any relations connected
     * if you want the database agent then use {@link Agent#thisDBAgent() }
     *
     * @return the current logged-in agent extracted from the JWT
     * @see Agent#thisDBAgent()
     */
    public static Agent thisAgent() {
        return (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * @return the current agent retrieved from the database
     */
    public static Agent thisDBAgent() {
        return Application.context.getBean(AgentRepository.class).findById(thisAgent().getId()).orElseThrow();
    }


    /**
     * @return the agent loaded from the database
     */
    public static Agent getByIdentifier(long id) {
        return Application.context.getBean(AgentRepository.class).findById(id).orElseThrow();
    }

    /**
     * @return the agent loaded from the database
     */
    public static Agent getByIdentifier(String id) {
        return getByIdentifier(Long.parseLong(id));
    }

    /**
     * @return the agent loaded from the database
     */
    public static Agent getByIdentifier(Agent id) {
        return getByIdentifier(id.getId());
    }
}
