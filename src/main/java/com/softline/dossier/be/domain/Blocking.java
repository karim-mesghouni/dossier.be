package com.softline.dossier.be.domain;

import com.softline.dossier.be.graphql.types.input.BlockingInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
@DynamicUpdate// only generate sql statement for changed columns
@SelectBeforeUpdate// only detached entities will be selected
public class Blocking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn
    private FileTaskSituation state;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    private BlockingLockingAddress lockingAddress;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    private BlockingQualification qualification;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn
    private BlockingLabel label;
    private String explication;
    private LocalDateTime date;
    private LocalDateTime dateUnBlocked;

    public static Blocking buildFromInput(BlockingInput input, FileTaskSituation state) {
        return Blocking.builder()
                .id(input.getId())
                .label(BlockingLabel.builder().id(input.getLabel().getId()).build())
                .lockingAddress(BlockingLockingAddress.builder().id(input.getLockingAddress().getId()).build())
                .qualification(BlockingQualification.builder().id(input.getQualification().getId()).build())
                .explication(input.getExplication())
                .dateUnBlocked(input.getDateUnBlocked())
                .state(state)
                .date(input.getDate())
                .build();
    }

    // can be used to determine if the block is active
    // used by graphql
    public boolean getBlock() {
        return this.dateUnBlocked == null;
    }
}
