package com.softline.dossier.be.domain;

import com.softline.dossier.be.Tools.EnvUtil;
import com.softline.dossier.be.Tools.FileSystem;
import com.softline.dossier.be.Tools.Functions;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public interface Attachment {
    LocalDateTime getCreatedDate();

    void setCreatedDate(LocalDateTime createdDate);

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

    void afterRemove();

    void afterCreating();
}
