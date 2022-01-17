package com.softline.dossier.be.domain;

import com.softline.dossier.be.Tools.EnvUtil;
import com.softline.dossier.be.Tools.FileSystem;
import com.softline.dossier.be.Tools.Functions;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Attachment {
    String getRealName();

    void setRealName(String realName);

    String getStorageName();

    void setStorageName(String storageName);

    String getContentType();

    void setContentType(String contentType);

    // used by graphql
    @SuppressWarnings("unused")
    default String getUrl() {
        return EnvUtil.getServerUrl() + "/attachments/" + getStorageName();
    }

    default Path getPath() {
        return FileSystem.getAttachmentsPath().resolve(getStorageName());
    }

    Functions.UnsafeRunnable getAfterCreate();

    void setAfterCreate(Functions.UnsafeRunnable afterCreate);


    default void resolveFromApplicationPart(ApplicationPart file) {
        setRealName(file.getSubmittedFileName());
        setStorageName(FileSystem.randomMD5() + "." + FilenameUtils.getExtension(file.getSubmittedFileName()));
        setContentType(file.getContentType());
        Path newPath = FileSystem.getAttachmentsPath().resolve(getStorageName());
        setAfterCreate(() -> {
            Files.copy(file.getInputStream(), newPath);
            file.delete();
        });
    }

    @PostRemove
    default void afterRemove() {
        if (getPath().toFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            getPath().toFile().delete();
        }
    }

    @PostPersist
    default void afterCreating() {
        if (getAfterCreate() != null) {
            Functions.wrap(getAfterCreate());
        }
    }
}
