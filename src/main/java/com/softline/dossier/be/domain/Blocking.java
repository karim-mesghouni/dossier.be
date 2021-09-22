package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE Blocking SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")

public class Blocking extends BaseEntity {
    boolean block;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn
    private FileTaskSituation state;
    @ManyToOne
    @JoinColumn
    private BlockingLockingAddress lockingAddress;
    @ManyToOne
    @JoinColumn
    private BlockingQualification qualification;
    @ManyToOne
    @JoinColumn
    private BlockingLabel label;
    private String explication;
    private LocalDateTime date;
}
