package com.softline.dossier.be.security.domain;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.security.repository.AgentRepository;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static com.softline.dossier.be.Application.getBean;
import static com.softline.dossier.be.Tools.Functions.notEmpty;

@SuperBuilder
@Entity
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@NamedQuery(name = "Agent.findBySearch", query = "SELECT a FROM Agent a where a.deleted = false and (a.username like CONCAT('%', :search, '%') or a.name like CONCAT('%', :search, '%')) ORDER BY a.createdDate DESC")
@SQLDelete(sql = "UPDATE agent SET deleted=true WHERE id=?")
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class Agent extends BaseEntity {
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

    @OneToMany(mappedBy = "agent")
    @ToString.Exclude
    private List<File> createdFiles;

    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }

    public boolean isAdmin() {
        return getRole() != null && this.getRole().is(Role.Type.MANAGER);
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
     * @return the current request-token-agent retrieved from the database
     * @throws EntityNotFoundException if the corresponding agent_id in the request token does not exist in the database
     */
    public static Agent thisDBAgent() throws EntityNotFoundException {
        return Database.findOrThrow(Agent.class, thisAgent());
    }


    /**
     * @return the agent loaded from the database
     */
    public static Agent getByIdentifier(long id) {
        return getBean(AgentRepository.class).findById(id).orElseThrow();
    }

    /**
     * @return the agent loaded from the database
     */
    public static Agent getByIdentifier(String id) {
        return getByIdentifier(Long.parseLong(id));
    }

    /**
     * @return the authentication extracted from the token
     */
    public static Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * @return true if the request contains a valid jwt token with user data
     */
    public static boolean isLoggedIn() {
        return notEmpty(() -> thisAgent().getId());// will return false if the value retrieval fails
    }

    /**
     * @return false if the request contains a valid jwt token with user data
     */
    public static boolean notLoggedIn() {
        return !isLoggedIn();
    }

    public boolean is(Role.Type type) {
        return getRole() != null && getRole().is(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Agent agent = (Agent) o;
        return Objects.equals(id, agent.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
