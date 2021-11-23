package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Attachment;
import lombok.Getter;

@Getter
public class AttachmentInput extends Input<Attachment> {
    Class<Attachment> mappingTarget = Attachment.class;

    String name;
}
