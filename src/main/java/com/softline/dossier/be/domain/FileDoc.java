package com.softline.dossier.be.domain;

import com.softline.dossier.be.security.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.MediaType;

import javax.persistence.*;
import java.text.MessageFormat;
import java.util.function.Function;

@SuperBuilder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class FileDoc extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    private String  path;
    @ManyToOne
    @JoinColumn
    File file;
    @ManyToOne
    @JoinColumn
    FileActivity  fileActivity;
    @ManyToOne
    @JoinColumn
    Agent agent;

     String description;
}
