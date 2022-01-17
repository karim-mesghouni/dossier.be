package com.softline.dossier.be;

import com.softline.dossier.be.Tools.Functions;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Attachment;
import com.softline.dossier.be.domain.ControlSheet;
import com.softline.dossier.be.domain.FileTaskAttachment;
import javassist.NotFoundException;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Responsible for serving files in the storage directory
 */
@Controller
public class StorageServer {
    @Value("${filesystem.storage.absolute-path}")
    String storagePath;

    @GetMapping("/assets/{asset}")
    public FileSystemResource assetsServer(@PathVariable String asset) {
        return new FileSystemResource(Paths.get(storagePath, "assets", asset).toFile());
    }

    @GetMapping("/attachments/{attachment}")
    public HttpEntity<InputStreamSource> attachmentsServer(@PathVariable String attachment) throws IOException, NotFoundException {
        Attachment target = findAttachment(attachment);
        HttpHeaders headers = new HttpHeaders();
        if (target != null) {
            headers.setContentDisposition(ContentDisposition.builder("inline")
                    .filename(target.getRealName())
                    .build());
            headers.setContentType(MediaType.parseMediaType(target.getContentType()));
        }
        var path = target != null ? target.getPath() : Paths.get(storagePath, "attachments", attachment);
        if (target == null) {
            headers.setContentType(MediaType.parseMediaType(Files.probeContentType(path)));
        }
        return new HttpEntity<>(new FileSystemResource(path), headers);
    }

    @Nullable
    private Attachment findAttachment(String storageName) {
        Attachment attachment = null;
        for (var klass : List.of(ControlSheet.class, FileTaskAttachment.class)) {
            attachment = Functions.safeValue(() -> Database.querySingle("SELECT a FROM " + klass.getName() + " a where a.storageName = :storageName", klass).setParameter("storageName", storageName).getSingleResult());
            if (attachment != null)
                break;
        }
        return attachment;
    }
}
