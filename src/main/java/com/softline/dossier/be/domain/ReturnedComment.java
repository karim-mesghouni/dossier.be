package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@DiscriminatorValue("Returned")
@SuperBuilder
@AllArgsConstructor
@Data
public class ReturnedComment extends Comment {


}
