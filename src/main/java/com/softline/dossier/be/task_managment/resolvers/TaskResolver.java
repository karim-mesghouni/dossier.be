package com.softline.dossier.be.task_managment.resolvers;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
public class TaskResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    Task saveTask(TaskInput input){

    }
}
