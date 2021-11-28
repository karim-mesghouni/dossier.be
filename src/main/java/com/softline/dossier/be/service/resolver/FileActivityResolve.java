package com.softline.dossier.be.service.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.domain.FileActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class FileActivityResolve implements GraphQLResolver<FileActivity> {
    public String getCreatedDate(FileActivity fileActivity) {
        if (fileActivity.getCreatedDate() != null) {
            return fileActivity.getCreatedDate().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"));
        }
        return null;
    }
}
