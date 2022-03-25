package com.softline.dossier.be;

import org.junit.jupiter.api.Test;


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