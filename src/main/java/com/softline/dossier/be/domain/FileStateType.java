package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@SQLDelete(sql = "UPDATE FileStateType SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class FileStateType extends BaseEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private  long id;
   String state;
   boolean initial;
   boolean Final;

   @Override
   public String toString() {
      return "FileStateType{" +
              "id=" + id +
              ", state='" + state + '\'' +
              '}';
   }
}
