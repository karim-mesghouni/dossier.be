package com.softline.dossier.be.domain;

import com.softline.dossier.be.Tools.EnvUtil;
import com.softline.dossier.be.Tools.FileSystem;
import com.softline.dossier.be.Tools.Functions;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.Transient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@ToString
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

    @Transient
    protected Functions.UnsafeRunnable afterCreate;

    @PostRemove
    public void removeFile() {
        if (getPath().toFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            getPath().toFile().delete();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Attachment that = (Attachment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    public void resolveFromApplicationPart(ApplicationPart file) {
        setRealName(file.getSubmittedFileName());
        setStorageName(FileSystem.randomMD5() + "." + FilenameUtils.getExtension(file.getSubmittedFileName()));
        setContentType(file.getContentType());
        Path newPath = FileSystem.getAttachmentsPath().resolve(storageName);
        this.afterCreate = () -> {
            Files.copy(file.getInputStream(), newPath);
            file.delete();
        };
    }

    @PostPersist
    public void afterCreating() {
        Functions.wrap(this.afterCreate);
    }
}
