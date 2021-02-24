package com.softline.dossier.be.db;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.FieldType;
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
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    CommuneRepository communeRepository;
    @Autowired
    AgentRepository agentRepository;
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
                     first.getPhases().add(Phase.builder().name(j==0?"Etude":"Controle").jobs(jobs).states(phaseStates).build());
                 }
                 if (i==0) {
                     var fields = new ArrayList<ActivityField>();
                     fields.add(ActivityField.builder().fieldName("Nombre EL client").fieldType(FieldType.Number).activity(first).build());
                     fields.add(ActivityField.builder().fieldName("Nombre EL").fieldType(FieldType.Number).activity(first).build());
                     fields.add(ActivityField.builder().fieldName("Nombre FOA").fieldType(FieldType.Number).activity(first).build());
                     first.setFields(fields);
                 }
                 first.getPhases().forEach(x->{
                     x.getJobs().forEach(j->j.setPhase(x));
                     x.getStates().forEach(s->s.setPhase(x));
                     x.setActivity(first);
                 });
                 activityRepository.save(first);
             }

         }
        if (clientRepository.count()==0){
            clientRepository.save(Client.builder().name("Orange").build());
            clientRepository.save(Client.builder().name("ALG Telecom").build());
            clientRepository.save(Client.builder().name("TN Telecom").build());
        }
        if(communeRepository.count()==0){
            communeRepository.save(Commune.builder().commune("commune 1").INSEECode("INSEE 39418").postalCode("39418").build());
            communeRepository.save(Commune.builder().commune("commune 2").INSEECode("INSEE 394016").postalCode("394016").build());
            communeRepository.save(Commune.builder().commune("commune 3").INSEECode("INSEE 500124").postalCode("500124").build());
            communeRepository.save(Commune.builder().commune("commune 4").INSEECode("INSEE 324821").postalCode("324821").build());
        }
        if(agentRepository.count()==0){
            agentRepository.save(Agent.builder().name("Elhabib").build());
            agentRepository.save(Agent.builder().name("Othman").build());
            agentRepository.save(Agent.builder().name("Djaber").build());
        }
    }
}
