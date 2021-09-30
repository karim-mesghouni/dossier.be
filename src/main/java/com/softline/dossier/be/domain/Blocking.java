package com.softline.dossier.be.domain;

import com.softline.dossier.be.graphql.types.input.BlockingInput;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
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
    private LocalDateTime dateUnBlocked;

    // can be used to determine if the block is active
    // used by graphql
    public boolean getBlock()
    {
        return this.dateUnBlocked == null;
    }

    public static Blocking buildFromInput(BlockingInput input, FileTaskSituation state)
    {
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
}
