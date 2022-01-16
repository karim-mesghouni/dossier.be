package com.softline.dossier.be.domain;

import com.softline.dossier.be.domain.Concerns.HasId;
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
public class BlockingLockingAddress implements HasId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToMany(mappedBy = "lockingAddress")
    List<Blocking> blocking;
    @OneToMany(mappedBy = "lockingAddress")
    List<VisAVis> visAVis;
    String address;

    @Override
    public String toString() {
        return "BlockingLockingAddress{" +
                "address='" + address + '\'' +
                '}';
    }
}
