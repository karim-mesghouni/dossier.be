package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE Blocking SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")

public class Blocking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn
    FileTaskSituation state;
    @ManyToOne
    @JoinColumn
    BlockingLockingAddress lockingAddress;
    @ManyToOne
    @JoinColumn
    BlockingQualification qualification;
    @ManyToOne
    @JoinColumn
    BlockingLabel label;
}
