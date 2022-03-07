package com.softline.dossier.be.task_managment.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
public class InstanceTask extends GenericTask{

}
