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
public class BlockingLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToMany(mappedBy = Blocking_.LABEL)
    List<Blocking> blocking;
    String name;

    @Override
    public String toString() {
        return "BlockingLabel{" +
                "name='" + name + '\'' +
                '}';
    }
}
