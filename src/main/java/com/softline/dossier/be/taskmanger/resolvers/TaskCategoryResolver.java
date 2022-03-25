package com.softline.dossier.be.taskmanger.resolvers;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.taskmanger.domain.TaskCategory;
import com.softline.dossier.be.taskmanger.types.input.TaskCategoryInput;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskCategoryResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    public List<TaskCategory> findAllTaskCategories(){
        return Database.findAll(TaskCategory.class);
    }

    public TaskCategory createTaskCategory(TaskCategoryInput input){
        return Database.inTransaction(() ->  Database.persist(input.map()));
    }
}
