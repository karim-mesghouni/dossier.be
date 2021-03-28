package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


public class ActivityFieldGroupInput  {

    long id;
    String group;
}
