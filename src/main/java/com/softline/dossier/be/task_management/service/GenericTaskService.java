package com.softline.dossier.be.task_management.service;


import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.task_management.domain.GenericTask;
import com.softline.dossier.be.task_management.types.input.GenericTaskInput;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenericTaskService {

    public GenericTask getById(Long id) {
        return Database.findOrThrow(GenericTask.class, id);
    }

    public static List<GenericTask> getAll() {return Database.findAll(GenericTask.class);}

    public GenericTask create(GenericTaskInput input){
        var genericTask = GenericTask.builder()
                .startDate(LocalDateTime.now())
                .name(input.getName())
                .category(input.getCategory())
                .daysOfWeek(input.getDaysOfWeek())
                .months(input.getMonths())
                .description(input.getDescription()).build();
        Database.startTransaction();
        Database.persist(genericTask);
        Database.commit();
        return genericTask;
    }
}
