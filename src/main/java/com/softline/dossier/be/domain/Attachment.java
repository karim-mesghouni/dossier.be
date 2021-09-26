package com.softline.dossier.be.domain;

import com.softline.dossier.be.Halpers.FileSystem;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.nio.file.Path;

@SuperBuilder
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE Attachment SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class Attachment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String realName;
    private String storageName;
    private String contentType;

    @SneakyThrows
    // used by graphql
    public String getUrl() {
        return "/attachments/" + getStorageName();
    }

    public Path getPath(FileSystem fileSystem) {
        return fileSystem.getAttachmentsPath().resolve(getStorageName());
    }
}
