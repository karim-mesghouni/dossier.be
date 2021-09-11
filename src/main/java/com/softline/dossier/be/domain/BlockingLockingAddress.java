package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE BlockingLockingAddress SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class BlockingLockingAddress  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @OneToMany(mappedBy=Blocking_.LOCKING_ADDRESS)
    List<Blocking> blocking;
    @OneToMany(mappedBy = VisAVis_.LOCKING_ADDRESS)
    List<VisAVis> visAVis;
    String address;

}
