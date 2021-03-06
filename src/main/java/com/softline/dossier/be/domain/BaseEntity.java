package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.Concerns.HasCreator;
import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.security.domain.Agent;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Getter
@Setter
@RequiredArgsConstructor
public class BaseEntity implements HasId, HasCreator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(columnDefinition = "boolean default false")
    protected boolean deleted;
    @Column(nullable = false, updatable = false)
    @CreatedDate
    protected LocalDateTime createdDate;
    @Column()
    @LastModifiedDate
    protected LocalDateTime modifiedDate;
    @CreatedBy
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    Agent agent;

    @Override
    public Agent getCreator() {
        return getAgent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && Objects.equals(that.id, id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * @return true if this entity was created by the given user
     */
    public boolean createdBy(Agent agent) {
        return agent != null && agent.equals(getCreator());
    }

    /**
     * @return true if this entity was created by the user who sent this request
     */
    public boolean createdByThisAgent() {
        return Agent.thisDBAgent().equals(getCreator());
    }
}
