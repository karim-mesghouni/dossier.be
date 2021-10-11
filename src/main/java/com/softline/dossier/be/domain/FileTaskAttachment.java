package com.softline.dossier.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@SuperBuilder
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class FileTaskAttachment extends Attachment
{
    @ManyToOne
    private FileTask fileTask;
}
