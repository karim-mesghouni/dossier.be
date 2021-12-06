package com.softline.dossier.be.domain;

import com.softline.dossier.be.Tools.EnvUtil;
import com.softline.dossier.be.Tools.FileSystem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import java.nio.file.Path;

@SuperBuilder
@AllArgsConstructor
@Data

@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE Attachment SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class Attachment extends BaseEntity {
    private String realName;
    private String storageName;
    private String contentType;

    // used by graphql
    public String getUrl() {
        return EnvUtil.getServerUrl() + "/attachments/" + getStorageName();
    }

    public Path getPath() {
        return FileSystem.getAttachmentsPath().resolve(getStorageName());
    }
}
