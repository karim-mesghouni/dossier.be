package com.softline.dossier.be.security.domain;

import com.softline.dossier.be.domain.BaseEntity;
import com.softline.dossier.be.domain.Comment;
import com.softline.dossier.be.domain.Comment_;
import com.softline.dossier.be.domain.Notification;
import com.softline.dossier.be.security.domain.casl.CaslRawRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Agent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;

    @OneToMany(mappedBy = Comment_.AGENT)
    List<Comment> comments;
    @OneToMany(mappedBy = "agent")
    List<Notification> notifications;
    @Transient()
    List<String> authorities;
    @Transient()
    List<CaslRawRule> caslRules;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "token_expired")
    private boolean tokenExpired;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;
}
