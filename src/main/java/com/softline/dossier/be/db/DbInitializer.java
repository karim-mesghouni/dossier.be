package com.softline.dossier.be.db;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.repository.*;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Privilege;
import com.softline.dossier.be.security.domain.Role;
import com.softline.dossier.be.security.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.expression.Lists;

import java.util.*;

@Component
public class DbInitializer implements ApplicationRunner {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    FileStateTypeRepository fileStateTypeRepository;

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

    @Autowired
    ReturnedCauseRepository returnedCauseRepository;

    @Autowired
    ActivityStateRepository activityStateRepository;

    Activity zapa;
    Activity fi;
    Activity ipon;
    Activity piquetage;
    Activity cdc;

    PasswordEncoder passwordEncoder;
    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        passwordEncoder=  PasswordEncoderFactories.createDelegatingPasswordEncoder();
        if (activityRepository.count() == 0) {

            createZapaActivity();
            createFIActivity();
            createIPONActivity();
            createPiquetageActivity();
            createCDCActivity();
        }
        if (clientRepository.count() == 0) {
            clientRepository.save(Client.builder().name("RH").build());
            clientRepository.save(Client.builder().name("AXIANS").build());
            clientRepository.save(Client.builder().name("AXIANS IDF").build());
            clientRepository.save(Client.builder().name("COVAGE").build());
            clientRepository.save(Client.builder().name("CPCP ROGNAC").build());
            clientRepository.save(Client.builder().name("CPCP SUD").build());
            clientRepository.save(Client.builder().name("FREE").build());
            clientRepository.save(Client.builder().name("HAUWEI").build());
            clientRepository.save(Client.builder().name("NET DESIGNER").build());
            clientRepository.save(Client.builder().name("NET GEO").build());
            clientRepository.save(Client.builder().name("OPT").build());
            clientRepository.save(Client.builder().name("OPTTICOM").build());
            clientRepository.save(Client.builder().name("S30").build());
            clientRepository.save(Client.builder().name("SCOPELEC").build());
            clientRepository.save(Client.builder().name("SPIE").build());
        }
        if (communeRepository.count() == 0) {
            createCommunes();
            }
        if (fileStateTypeRepository.count() == 0) {
            fileStateTypeRepository.save(FileStateType.builder().state("En cours").initial(true).build());
            fileStateTypeRepository.save(FileStateType.builder().state("Terminé").Final(true).build());
            fileStateTypeRepository.save(FileStateType.builder().state("Livré").build());
            fileStateTypeRepository.save(FileStateType.builder().state("À LIVRER").build());
            fileStateTypeRepository.save(FileStateType.builder().state("RETIRÉ").build());
            fileStateTypeRepository.save(FileStateType.builder().state("STANDBY").build());
            fileStateTypeRepository.save(FileStateType.builder().state("PREFIBRÉ").build());
            fileStateTypeRepository.save(FileStateType.builder().state("À RETIRER").build());
            fileStateTypeRepository.save(FileStateType.builder().state("STANDBY CLIENT").build());
            fileStateTypeRepository.save(FileStateType.builder().state("MANQUANT").build());
            fileStateTypeRepository.save(FileStateType.builder().state("REPRISE PIQUETAGE").build());
            fileStateTypeRepository.save(FileStateType.builder().state("NON AFFECTÉ ÉTUDE").build());
            fileStateTypeRepository.save(FileStateType.builder().state("REPRISE EN COURS D'ETUDE").build());
            fileStateTypeRepository.save(FileStateType.builder().state("KIZÉO NON ATTRIBUÉ").build());
            fileStateTypeRepository.save(FileStateType.builder().state("ANNULÉ").Final(true).build());

        }
        if (agentRepository.count()==0){
            var agents=List.of(
                    "elhabib"
                    ,"othman"
                    ,"ELTIFI Sana"
                    ,"HMAIDI Omar"
                    ,"BENNOUR Imen"
                    ,"AZIZI Chaima"
                    ,"HAJRI Khaoula"
                    ,"MABROUK Akrem"
                    ,"BEN AMOR Talel"
                    ,"KHEZAMI Aymen"
                    ,"TARHOUNI Donia"
                    ,"SININI Yosra"
                    ,"ELKEFI Salma"
                    ,"KOCHBATI Ep KAMOUN Nouha"
                    ,"AMDOUNI Med Ali"
                    ,"SASSI Olfa"
                    ,"AROUI Mahdi"
                    ,"LOUATI Ikbel"
                    ,"JAMAI Hiba"
                    ,"TOUZRI Jamil Aziz"
                    ,"SAID Mouhamed"
                    ,"BOULILA Fatma"
                    ,"DAKHLAOUI Rahma"
                    ,"BEN HLIMA Omar"
                    ,"NEMRI Ep. ELOUSGI Sarra"
                    ,"AGREBI Yosra"
                    ,"MELKI Maroua"
                    ,"MEJRI AFEF"
                    ,"BEN RACHED Oumayma"
                    ,"KAROUI Salim"
                    ,"BEJAOUI Nadia"
                    ,"AISSAOUI Mohamed Sofiene"
                    ,"OUESLATI Mariem"
                    ,"GUITOUNI Raoua"
                    ,"MASMOUDI Ines"
                    ,"HAMMAMI Aziza"
                    ,"HAJJI Tasnim"
                    ,"TAYEG Ghada"
                    ,"HOSNY Sawssen"
                    ,"BEN SALAH Ep. MOUSSA Mariem"
                    ,"CHAMMEM Manel"
                    ,"NEGUIA SalahEddine"
                    ,"DIOUANE Amor"
                    ,"GHEZALI Mahmoud"
                    ,"CHIHI Rihem"
                    ,"CHIHI Amal"
                    ,"BRAHMI Asma"
                    ,"LABIDI Khawla"
                    ,"BEN ELBEY Lobna"
                    ,"MAAROUFI Wissal"
                    ,"HAMMAMI Ines"
                    ,"KAABACHI Khaled"
                    ,"DAHMENI Ahmed"
                    ,"Cpcp"
                    ,"Nasri"
                    ,"Rafaa"
                    ,"Julien"
                    ,"Riahi Safa"
                    ,"Wael+Firas"
                    ,"Jelassi Wael"
                    ,"Hmaied Firas"
                    ,"Bennour Ramzi"
                    ,"Riahi Mohamed"
                    ,"Settou Mohamed"
                    ,"Belhaj Med Souhaieb"
                    ,"Hermi Ali"
                    ,"Mezni Emna"
                    ,"Sbai Malek"
                    ,"Abcha Amani"
                    ,"Aroui Mehdi"
                    ,"Lakti Marwa"
                    ,"Melki Marwa"
                    ,"Jlassi Wael"
                    ,"Souissi Beya"
                    ,"Senini Yosra"
                    ,"Ferchichi Aya"
                    ,"Khaldi Khawla"
                    ,"Touihri Nouha"
                    ,"Khaldi Yosra "
                    ,"Si Jemaa Akrem"
                    ,"Hannachi Fadwa"
                    ,"Karoui Mohamed"
                    ,"Riahi Mohamed "
                    ,"Hedidar Naouel"
                    ,"Nahali Nesrine"
                    ,"Mathlouthi Amel"
                    ,"Romdhani Chaima"
                    ,"Khelifi Ghassen"
                    ,"Bouhlel Oussema");
            for (var agent:agents) {

                agentRepository.save(Agent.builder()
                        .name(agent)
                        .email(agent+"@gmail.com")
                        .username(agent.replace(" ","_"))
                        .password(passwordEncoder.encode("000"))
                        .enabled(true)
                        .roles(List.of(
                                Role.builder().name("Role_Manger").privileges(
                                        List.of(Privilege.builder().name("View_Activity").build())
                                ).build()
                        ))
                        .build()

                );
            }

        }
    }
    private void createCommunes() {
        communeRepository.save(Commune.builder().name("BOURG EN BRESSE").INSEECode("01053").postalCode("1000").build());
        communeRepository.save(Commune.builder().name("SAINT DENIS LES BOURG").INSEECode("01344").postalCode("1000").build());
        communeRepository.save(Commune.builder().name("BROU").INSEECode("01914").postalCode("1000").build());
        communeRepository.save(Commune.builder().name("AMAREINS").INSEECode("01003").postalCode("1090").build());
        communeRepository.save(Commune.builder().name("CESSEINS").INSEECode("01070").postalCode("1090").build());
        communeRepository.save(Commune.builder().name("AMAREINS FRANCHELEINS CES").INSEECode("01165").postalCode("1090").build());
        communeRepository.save(Commune.builder().name("GENOUILLEUX").INSEECode("01169").postalCode("1090").build());
        communeRepository.save(Commune.builder().name("GUEREINS").INSEECode("01183").postalCode("1090").build());


    }
    private void createZapaActivity() {
        zapa = Activity.builder().name("ZAPA").description("ZAPA Description").tasks(new ArrayList<>()).build();
        var taskSituationsEtude = new ArrayList<TaskSituation>();
        taskSituationsEtude.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsEtude.add(TaskSituation.builder().name("En cours").build());
        taskSituationsEtude.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsEtude.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var taskSituationsPreparatrionLivraison= new ArrayList<TaskSituation>();
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("En cours").build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var PreparatrionLivraison = Task.builder().name("Préparatrion de livraison").situations(taskSituationsPreparatrionLivraison).activity(zapa).build();
        PreparatrionLivraison.getSituations().forEach(x->x.setTask(PreparatrionLivraison));
        var states = new ArrayList();
        states.add(TaskState.builder().name("Valide").build());
        states.add(TaskState.builder().name("Non valide").build());
        var etdue = Task.builder().name("Etude").situations(taskSituationsEtude).activity(zapa).build();
        var controle = Task.builder().name("Controle").situations(taskSituationsControle).activity(zapa).states(states).build();
        etdue.getSituations().forEach(x -> x.setTask(etdue));

        var activityStates = new ArrayList();
        activityStates.add(ActivityState.builder().activity(zapa).name("En cours").initial(true).build());
        activityStates.add(ActivityState.builder().activity(zapa).name("PREFIBRÉ").build());
        activityStates.add(ActivityState.builder().activity(zapa).name("Terminé").Final(true).build());
        zapa.setStates(activityStates);


        controle.getSituations().forEach(x -> x.setTask(controle));
        controle.getStates().forEach(x -> x.setTask(controle));
        zapa.getTasks().add(etdue);
        zapa.getTasks().add(controle);
        zapa.getTasks().add(PreparatrionLivraison);
        var fields = new ArrayList<ActivityField>();
        fields.add(ActivityField.builder().fieldName("CEM").fieldType(FieldType.String).activity(zapa).build());
        fields.add(ActivityField.builder().fieldName("EL ZAPA TOT").fieldType(FieldType.String).activity(zapa).build());
        fields.add(ActivityField.builder().fieldName("NBRE EL Client").fieldType(FieldType.String).activity(zapa).build());
        fields.add(ActivityField.builder().fieldName("NBRE EL Etudiant").fieldType(FieldType.String).activity(zapa).build());
        fields.add(ActivityField.builder().fieldName("NBRE FOA").fieldType(FieldType.String).activity(zapa).build());
        zapa.setFields(fields);
        activityRepository.save(zapa);
    }
    private void createFIActivity() {
         fi = Activity.builder().name("FI").description("FI Description").tasks(new ArrayList<>()).build();
        var taskSituationsEtude = new ArrayList<TaskSituation>();
        taskSituationsEtude.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsEtude.add(TaskSituation.builder().name("En cours").build());
        taskSituationsEtude.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsEtude.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var taskSituationsPreparatrionLivraison= new ArrayList<TaskSituation>();
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("En cours").build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var PreparatrionLivraison = Task.builder().name("Préparatrion de livraison").situations(taskSituationsPreparatrionLivraison).activity(fi).build();
        PreparatrionLivraison.getSituations().forEach(x->x.setTask(PreparatrionLivraison));
        var states = new ArrayList();
        states.add(TaskState.builder().name("Valide").build());
        states.add(TaskState.builder().name("Non valide").build());
        var etdue = Task.builder().name("Etude").situations(taskSituationsEtude).activity(fi).build();
        var controle = Task.builder().name("Controle").situations(taskSituationsControle).activity(fi).states(states).build();
        etdue.getSituations().forEach(x -> x.setTask(etdue));
        controle.getSituations().forEach(x -> x.setTask(controle));
        controle.getStates().forEach(x -> x.setTask(controle));

        var activityStates = new ArrayList();
        activityStates.add(ActivityState.builder().activity(fi).name("En cours").initial(true).build());
        activityStates.add(ActivityState.builder().activity(fi).name("PREFIBRÉ").build());
        activityStates.add(ActivityState.builder().activity(fi).name("Terminé").Final(true).build());
        fi.setStates(activityStates);


        fi.getTasks().add(etdue);
        fi.getTasks().add(controle);
        fi.getTasks().add(PreparatrionLivraison);
        var fields = new ArrayList<ActivityField>();
        fields.add(ActivityField.builder().fieldName("CEM").fieldType(FieldType.String).activity(fi).build());
        fields.add(ActivityField.builder().fieldName("IMB").fieldType(FieldType.String).activity(fi).build());
        fields.add(ActivityField.builder().fieldName("FIS").fieldType(FieldType.String).activity(fi).build());
        fields.add(ActivityField.builder().fieldName("EL BE DL").fieldType(FieldType.String).activity(fi).build());
        fields.add(ActivityField.builder().fieldName("EL IMB / FIS").fieldType(FieldType.String).activity(fi).build());
        fi.setFields(fields);
        activityRepository.save(fi);
    }
    private void createIPONActivity()


    {
         ipon = Activity.builder().name("IPON").description("IPON Description").tasks(new ArrayList<>()).build();
        var taskSituationsEtude = new ArrayList<TaskSituation>();
        taskSituationsEtude.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsEtude.add(TaskSituation.builder().name("En cours").build());
        taskSituationsEtude.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsEtude.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var taskSituationsPreparatrionLivraison= new ArrayList<TaskSituation>();
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("En cours").build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var PreparatrionLivraison = Task.builder().name("Préparatrion de livraison").situations(taskSituationsPreparatrionLivraison).activity(ipon).build();
        PreparatrionLivraison.getSituations().forEach(x->x.setTask(PreparatrionLivraison));

        var activityStates = new ArrayList();
        activityStates.add(ActivityState.builder().activity(ipon).name("En cours").initial(true).build());
        activityStates.add(ActivityState.builder().activity(ipon).name("PREFIBRÉ").build());
        activityStates.add(ActivityState.builder().activity(ipon).name("Terminé").Final(true).build());
        ipon.setStates(activityStates);


        var states = new ArrayList();
        states.add(TaskState.builder().name("Valide").build());
        states.add(TaskState.builder().name("Non valide").build());
        var etdue = Task.builder().name("Etude").situations(taskSituationsEtude).activity(ipon).build();
        var controle = Task.builder().name("Controle").situations(taskSituationsControle).activity(ipon).states(states).build();
        etdue.getSituations().forEach(x -> x.setTask(etdue));
        controle.getSituations().forEach(x -> x.setTask(controle));
        controle.getStates().forEach(x -> x.setTask(controle));
        ipon.getTasks().add(etdue);
        ipon.getTasks().add(controle);
        ipon.getTasks().add(PreparatrionLivraison);
        activityRepository.save(ipon);
    }
    private void createPiquetageActivity() {
         piquetage = Activity.builder().name("Piquetage").description("Piquetage Description").tasks(new ArrayList<>()).build();
        var taskSituationsEtude = new ArrayList<TaskSituation>();
        taskSituationsEtude.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsEtude.add(TaskSituation.builder().name("En cours").build());
        taskSituationsEtude.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsEtude.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var taskSituationsverification = new ArrayList<TaskSituation>();
        taskSituationsverification.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsverification.add(TaskSituation.builder().name("En cours").build());
        taskSituationsverification.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsverification.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var states = new ArrayList();
        states.add(TaskState.builder().name("Valide").build());
        states.add(TaskState.builder().name("Non Valide").build());
        var preparation = Task.builder().name("PRÉPARATION").situations(taskSituationsEtude).activity(piquetage).build();
        var control = Task.builder().name("PIQUETAGE").situations(taskSituationsControle).activity(piquetage).build();
        var verification = Task.builder().name("VÉRIFICATION DE RETOUR ").situations(taskSituationsverification).activity(piquetage).states(states).build();

        var activityStates = new ArrayList();
        activityStates.add(ActivityState.builder().activity(piquetage).name("En cours").initial(true).build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("PREFIBRÉ").build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("Terminé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("PRISE DE RDV PIQUETAGE").build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("PIQUETÉ NON REÇU").build());
        piquetage.setStates(activityStates);

        preparation.getSituations().forEach(x -> x.setTask(preparation));
        control.getSituations().forEach(x -> x.setTask(control));
        verification.getSituations().forEach(x -> x.setTask(verification));
        verification.getStates().forEach(x -> x.setTask(verification));
        piquetage.getTasks().add(preparation);
        piquetage.getTasks().add(control);
        piquetage.getTasks().add(verification);
        var fields = new ArrayList<ActivityField>();
        fields.add(ActivityField.builder().fieldName("Poteaux FT").fieldType(FieldType.String).activity(piquetage).activityBase(zapa).build());
        fields.add(ActivityField.builder().fieldName("Poteaux ERDF").fieldType(FieldType.String).activity(piquetage).activityBase(zapa).build());
        fields.add(ActivityField.builder().fieldName("NBRE APPUI PIQUETÉS").fieldType(FieldType.String).activity(piquetage).activityBase(zapa).build());
        fields.add(ActivityField.builder().fieldName("IMB***").fieldType(FieldType.String).activity(piquetage).activityBase(fi).build());
        piquetage.setFields(fields);
        activityRepository.save(piquetage);
    }
    private void createCDCActivity() {
        cdc = Activity.builder().name("CDC").description("CDC Description").tasks(new ArrayList<>()).build();
        var taskSituationsEtdueComac = new ArrayList<TaskSituation>();
        taskSituationsEtdueComac.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsEtdueComac.add(TaskSituation.builder().name("En cours").build());
        taskSituationsEtdueComac.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsEtdueComac.add(TaskSituation.builder().name("Annulé").Final(true).build());

        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var taskSituationsPreparatrionLivraison= new ArrayList<TaskSituation>();
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("En cours").build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var PreparatrionLivraison = Task.builder().name("Préparatrion de livraison").situations(taskSituationsPreparatrionLivraison).activity(cdc).build();
        PreparatrionLivraison.getSituations().forEach(x->x.setTask(PreparatrionLivraison));
        var states = new ArrayList();
        states.add(TaskState.builder().name("Valide").build());
        states.add(TaskState.builder().name("Non valide").build());
        var etdueComac = Task.builder().name("Etdue").situations(taskSituationsEtdueComac).activity(cdc).build();
        var controle = Task.builder().name("Controle").situations(taskSituationsControle).activity(cdc).states(states).build();
        etdueComac.getSituations().forEach(x -> x.setTask(etdueComac));
        controle.getSituations().forEach(x -> x.setTask(controle));
        controle.getStates().forEach(x -> x.setTask(controle));
        cdc.getTasks().add(etdueComac);
        cdc.getTasks().add(controle);
        cdc.getTasks().add(PreparatrionLivraison);

        var activityStates = new ArrayList();
        activityStates.add(ActivityState.builder().activity(cdc).name("En cours").initial(true).build());
        activityStates.add(ActivityState.builder().activity(cdc).name("PREFIBRÉ").build());
        activityStates.add(ActivityState.builder().activity(cdc).name("Terminé").Final(false).build());
        cdc.setStates(activityStates);


        var groupFieldsCOMAC=ActivityFieldGroup.builder().name("COMAC").build();
        var fields = new ArrayList<ActivityField>();
        fields.add(ActivityField.builder().fieldName("Nombre  des Artères").fieldType(FieldType.String).group(groupFieldsCOMAC).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre  des appuis").fieldType(FieldType.String).group(groupFieldsCOMAC).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre d’Appuis implanter").fieldType(FieldType.String).group(groupFieldsCOMAC).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre de CRIT").fieldType(FieldType.String).activity(cdc).group(groupFieldsCOMAC).build());
        var groupFieldsCAPFT=ActivityFieldGroup.builder().name("CAPFT").build();

        fields.add(ActivityField.builder().fieldName("Nombre  des Artères").fieldType(FieldType.String).group(groupFieldsCAPFT).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre  des appuis").fieldType(FieldType.String).group(groupFieldsCAPFT).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre d’Appuis implanter").fieldType(FieldType.String).group(groupFieldsCAPFT).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre de CRIT").fieldType(FieldType.String).activity(cdc).group(groupFieldsCAPFT).build());
        fields.add(ActivityField.builder().fieldName("Nombre d’Appuis à remplacer").fieldType(FieldType.String).activity(cdc).group(groupFieldsCAPFT).build());
        cdc.setFields(fields);
        activityRepository.save(cdc);
    }
}
