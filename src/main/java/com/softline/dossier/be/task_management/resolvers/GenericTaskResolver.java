package com.softline.dossier.be.task_management.resolvers;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.task_management.domain.GenericTask;
import com.softline.dossier.be.task_management.domain.TaskCategory;
import com.softline.dossier.be.task_management.enums.Periodicity;
import com.softline.dossier.be.task_management.repository.GenericTaskRepository;
import com.softline.dossier.be.task_management.types.input.GenericTaskInput;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class GenericTaskResolver implements GraphQLResolver<GenericTask> {

    @Autowired
    public GenericTaskRepository genericTaskRepository;

    public Iterable<GenericTask> findAllGenericTask(){
        return genericTaskRepository.findAll();
    }

//    public GenericTask createGenericTask(GenericTaskInput input){
//        GenericTask genericTask = GenericTask.builder().
//                name(input.getName()).
//                description(input.getDescription())
//                .startDate(LocalDateTime.now()).
//                periodicity(Periodicity.OneTime)
//                .dueDate(LocalDateTime.now())
//                .category(TaskCategory.builder().id(1L).name("category1").build())
//                .build();
//        genericTaskRepository.save(genericTask);
//        return genericTask;
//    }

//    public GenericTask getGenericTask(Long id){
//       return Database.query("SELECT gt FROM GenericTask gt where gt.id = :genericTaskId", GenericTask.class)
//                .setParameter("genericTaskId", id)
//                .setMaxResults(1)
//                .getSingleResult();
//    }

}
