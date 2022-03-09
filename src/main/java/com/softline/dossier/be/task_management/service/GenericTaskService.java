package com.softline.dossier.be.task_management.service;


import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.FileTask;
import com.softline.dossier.be.domain.TaskState;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.FileTaskEvent;
import com.softline.dossier.be.task_management.domain.GenericTask;
import com.softline.dossier.be.task_management.types.input.GenericTaskInput;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.softline.dossier.be.Tools.Functions.safeRun;
import static com.softline.dossier.be.security.config.Gate.check;

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
//                .daysOfWeek(input.getDaysOfWeek())
//                .months(input.getMonths())
                .description(input.getDescription()).build();
        Database.startTransaction();
        Database.persist(genericTask);
        Database.commit();
        return genericTask;
    }

    public GenericTask update(GenericTaskInput input){
        var genericTask = Database.findOrThrow(GenericTask.class, input);
//        check("UPDATE_FILE_TASK", genericTask);
        Database.startTransaction();
        genericTask.setName(input.getName());
        genericTask.setDueDate(input.getDueDate());
        genericTask.setCategory(input.getCategory());
        genericTask.setDescription(input.getDescription());
        genericTask.setEndDate(input.getEndDate());
//        genericTask.setDaysOfWeek(input.getDaysOfWeek());
//        genericTask.setDaysOfMonth(input.map().getDaysOfMonth());
//        genericTask.setMonths(input.getMonths());
//        safeRun(() -> fileTask.setState(Database.findOrThrow(TaskState.class, input.getState())));
        Database.commit();
//        new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        return genericTask;
    }
}
