package com.softline.dossier.be;

import com.softline.dossier.be.Halpers.FileSystem;
import com.softline.dossier.be.domain.Attachment;
import com.softline.dossier.be.repository.CommentAttachmentRepository;
import com.softline.dossier.be.repository.FileTaskAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;

/**
 * This controller is responsible for serving attachments in the storage
 */
@Controller
@RequiredArgsConstructor
public class StorageController {
    private final FileTaskAttachmentRepository fileTaskAttachmentRepository;
    private final CommentAttachmentRepository commentAttachmentRepository;

    @GetMapping("/attachments/{storageName}")
    public ResponseEntity<InputStreamResource> getAttachmentByStorageName(@PathVariable String storageName) throws IOException {
        Attachment attachment;
        attachment = fileTaskAttachmentRepository.findByStorageName(storageName);
        if (attachment == null) {
            attachment = commentAttachmentRepository.findByStorageName(storageName);
        }
        if (attachment == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + attachment.getRealName());
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .body(new InputStreamResource(Files.newInputStream(FileSystem.getAttachmentsPath().resolve(attachment.getStorageName()))));
    }

    @GetMapping("/assets/{asset}")
    public ResponseEntity<InputStreamResource> getAsset(@PathVariable String asset) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        var path = FileSystem.getAssetsPath().resolve(asset);
        headers.add("Content-Disposition", "inline; filename=" + asset);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                .body(new InputStreamResource(Files.newInputStream(path)));
    }
}
