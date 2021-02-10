package com.softline.dossier.be.db;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DbInitializer implements ApplicationRunner {

    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    FileStateTypeRepository  fileStateTypeRepository;
    @Autowired
    BlockingLockingAddressRepository blockingLockingAddressRepository;
    @Autowired
    BlockingQualificationRepository blockingQualificationRepository;
    @Autowired
    BlockingLabelRepository blockingLabelRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {
         if(fileStateTypeRepository.count()==0){
             fileStateTypeRepository.save(FileStateType.builder().state("Attribué").build());
             fileStateTypeRepository.save(FileStateType.builder().state("Livré").build());
             fileStateTypeRepository.save(FileStateType.builder().state("Encours").build());
             fileStateTypeRepository.save(FileStateType.builder().state("Annulé per le client").build());
             fileStateTypeRepository.save(FileStateType.builder().state("Stand  par le client ").build());
         }
         if(activityRepository.count()==0){
             for (int i = 0; i <2; i++) {
                 var first=  Activity.builder().name(i==0?"ZAPA":"IPON").description(i==0?"ZAPA Description":"IPON Description").phases(new ArrayList<>()).build();
                 for (int j = 0; j < 2; j++) {
                     var jobs=new ArrayList<Job>();
                     jobs.add(Job.builder().name("Etudiant").build());
                     jobs.add(Job.builder().name("Piquetuer").build());
                     jobs.add(Job.builder().name("Referrnt").build());
                     jobs.add(Job.builder().name("Manager").build());
                     var phaseStates=new ArrayList<PhaseState>();
                     phaseStates.add(PhaseState.builder().state("A faire").initial(true).build());
                     phaseStates.add(PhaseState.builder().state("En cours").build());
                     phaseStates.add(PhaseState.builder().state("Fait").Final(true).build());
                     first.getPhases().add(Phase.builder().name(i==0?"Etude":"Controle").jobs(jobs).states(phaseStates).build());
                 }

                 first.getPhases().forEach(x->{
                     x.getJobs().forEach(j->j.setPhase(x));
                     x.getStates().forEach(s->s.setPhase(x));
                     x.setActivity(first);
                 });
                 activityRepository.save(first);
             }

         }
    }
}
