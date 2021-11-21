package com.softline.dossier.be.service.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.FileTask;
import com.softline.dossier.be.domain.FileTaskSituation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class FileTaskResolver implements GraphQLResolver<FileTask> {
    public FileTaskSituation getCurrentFileTaskSituation(FileTask fileTask) {
        return Database.query("SELECT fts FROM FileTaskSituation fts where fts.current = true and fts.fileTask.id = :fileTaskId", FileTaskSituation.class)
                .setParameter("fileTaskId", fileTask.getId())
                .setMaxResults(1)
                .getSingleResult();
    }

    public String getToStartDate(FileTask fileTask) {
        if (fileTask.getToStartDate() != null) {
            return fileTask.getToStartDate().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"));
        }
        return null;
    }

    public String getDueDate(FileTask fileTask) {
        if (fileTask.getDueDate() != null) {
            return fileTask.getDueDate().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"));
        }
        return null;

    }

    public String getStartDate(FileTask fileTask) {
        if (fileTask.getStartDate() != null) {
            return fileTask.getStartDate().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"));
        }
        return null;

    }

    public String getEndDate(FileTask fileTask) {
        if (fileTask.getEndDate() != null) {
            return fileTask.getEndDate().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"));
        }
        return null;
    }
}
