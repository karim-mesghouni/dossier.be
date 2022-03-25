package com.softline.dossier.be.taskmanger.resolvers;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.taskmanger.domain.GenericTask;
import com.softline.dossier.be.taskmanger.types.input.GenericTaskInput;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenericTaskResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    public List<GenericTask> findAllGenericTasks() {
        return Database.findAll(GenericTask.class);
    }

    public GenericTask createGenericTask(GenericTaskInput input) {
        return Database.inTransaction(() -> Database.persist(input.map()));
    }

    public GenericTask getGenericTask(Long id) {
        return Database.findOrThrow(GenericTask.class, id);
    }
}
