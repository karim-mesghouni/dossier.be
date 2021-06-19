package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.BaseEntity;
import com.softline.dossier.be.domain.FileTask;
import com.softline.dossier.be.graphql.types.input.FileTaskInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachFileInput  {

    private  long id;
    private String  path;
    public String name;

    FileTaskInput fileTask;
}
