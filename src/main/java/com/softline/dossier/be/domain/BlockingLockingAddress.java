package com.softline.dossier.be.domain;

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
public class BlockingLockingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
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
