package com.softline.dossier.be;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.task_management.domain.GenericTask;
import com.softline.dossier.be.task_management.domain.TaskCategory;
import com.softline.dossier.be.task_management.enums.Periodicity;
import com.softline.dossier.be.task_management.service.GenericTaskService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;


public class ApplicationTest {

//    @MockBean
//    static ConfigurableApplicationContext context;
//    @MockBean
//    static EntityManager em;
//    @TestConfiguration
//    static class GenericTaskServiceContextConfiguration{
//        @Bean
//        public GenericTaskService genericTaskService(){
//            return new GenericTaskService();
//        }
//        @Bean
//        public static EntityManager em() {
//            return context.getBean("localEntityManager", EntityManager.class);
//        }
//    }
    @Test
    void contextLoads() {

    }

//    @Test
//    public void whenGetAll_returnGenericTaskList(){
//        GenericTask task1 = GenericTask.builder()
//                .id(1L).name("task1")
//                .category(TaskCategory
//                        .builder()
//                        .id(2L)
//                        .name("category1")
//                        .build())
//                .startDate(LocalDateTime.now())
//                .periodicity(Periodicity.OneTime)
//                .endDate(LocalDateTime
//                        .now())
//                .build();
//        GenericTask task2 = GenericTask.builder().id(3L).name("task22").category(TaskCategory.builder().id(4L).name("category3").build())
//                .startDate(LocalDateTime.now()).periodicity(Periodicity.OneTime).endDate(LocalDateTime.now()).build();
//        em = context.getBean("localEntityManager", EntityManager.class);
//        em.getTransaction().begin();
//        em.persist(task1);
//        em.persist(task2);
//        em.flush();
//        if (em.getTransaction().isActive()) {
//            em.getTransaction().commit();
//        }
//
//        Assertions.assertThat(GenericTaskService.getAll().contains(task1));
//    }


}