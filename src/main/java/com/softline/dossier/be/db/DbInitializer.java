package com.softline.dossier.be.db;

import com.github.javafaker.Faker;
import com.softline.dossier.be.Halpers.ListUtils;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.repository.*;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Privilege;
import com.softline.dossier.be.security.domain.Role;
import com.softline.dossier.be.security.repository.AgentRepository;
import com.softline.dossier.be.security.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.expression.Lists;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DbInitializer implements ApplicationRunner{

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

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    RoleRepository roleRepository;


    Faker faker = new Faker(new Locale("fr"));

    Activity zapa;
    Activity fi;
    Activity ipon;
    Activity piquetage;
    Activity cdc;

    PasswordEncoder passwordEncoder;
    @Transactional

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
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("RH"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("AXIANS"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("AXIANS IDF"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("COVAGE"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("CPCP ROGNAC"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("CPCP SUD"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("FREE"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("NET DESIGNER"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("NET GEO"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("OPT"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("OPTTICOM"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("S30"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("SCOPELEC"))));
            contactRepository.saveAll(fakeContacts(2, clientRepository.save(fakeClient("SPIE"))));
        }
        if (communeRepository.count() == 0) {
            createCommunes();
            }
        if (fileStateTypeRepository.count() == 0) {
            fileStateTypeRepository.save(FileStateType.builder().state("En cours").build());
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
            fileStateTypeRepository.save(FileStateType.builder().state("NON AFFECTÉ").initial(true).build());
            fileStateTypeRepository.save(FileStateType.builder().state("REPRISE EN COURS D'ETUDE").build());
            fileStateTypeRepository.save(FileStateType.builder().state("KIZÉO NON ATTRIBUÉ").build());
            fileStateTypeRepository.save(FileStateType.builder().state("ANNULÉ").Final(true).build());

        }
        String c = "CREATE_", r = "READ_", u = "UPDATE_", d = "DELETE_";
//        var role = roleRepository.findOne(Example.of(Role.builder().name("ROLE_ADMIN").build())).get();
//        var privs = role.getPrivileges();
//        privs.addAll(List.of(Privilege.builder().name(c+"HISTORY").build(),
//                Privilege.builder().name(r+"HISTORY").build(),
//                Privilege.builder().name(u+"HISTORY").build(),
//                Privilege.builder().name(d+"HISTORY").build()));
//        role.setPrivileges(privs);
//        roleRepository.save(role);
        if (agentRepository.count() == 0)
        {
            final Role ADMIN_ROLE;

            List<Privilege> allPrivileges = List.of(
                    Privilege.builder().name(c + "FILE").build(),
                    Privilege.builder().name(r + "FILE").build(),
                    Privilege.builder().name(u + "FILE").build(),
                    Privilege.builder().name(d + "FILE").build(),

                    Privilege.builder().name(c + "TASK").build(),
                    Privilege.builder().name(r + "TASK").build(),
                    Privilege.builder().name(u + "TASK").build(),
                    Privilege.builder().name(d + "TASK").build(),

                    Privilege.builder().name(c + "CLIENT").build(),
                    Privilege.builder().name(r + "CLIENT").build(),
                    Privilege.builder().name(u + "CLIENT").build(),
                    Privilege.builder().name(d + "CLIENT").build(),

                    Privilege.builder().name(c + "CONTACT").build(),
                    Privilege.builder().name(r + "CONTACT").build(),
                    Privilege.builder().name(u + "CONTACT").build(),
                    Privilege.builder().name(d + "CONTACT").build(),

                    Privilege.builder().name(c + "ACTIVITY").build(),
                    Privilege.builder().name(r + "ACTIVITY").build(),
                    Privilege.builder().name(u + "ACTIVITY").build(),
                    Privilege.builder().name(d + "ACTIVITY").build(),

                    Privilege.builder().name(c + "HISTORY").build(),
                    Privilege.builder().name(r + "HISTORY").build(),
                    Privilege.builder().name(u + "HISTORY").build(),
                    Privilege.builder().name(d + "HISTORY").build(),

                    Privilege.builder().name(c + "ROLE").build(),
                    Privilege.builder().name(r + "ROLE").build(),
                    Privilege.builder().name(u + "ROLE").build(),
                    Privilege.builder().name(d + "ROLE").build(),

                        Privilege.builder().name(c+"Trash").build(),
                        Privilege.builder().name(r+"Trash").build(),
                        Privilege.builder().name(u+"Trash").build(),
                        Privilege.builder().name(d+"Trash").build()
                );
                ADMIN_ROLE = roleRepository.save(Role.builder().name("ROLE_ADMIN").privileges(allPrivileges).build());
            }
            // admin user
            for (var admin: List.of("elhabib", "othman", "boubaker"))
            {
                agentRepository.save(Agent.builder()
                        .name(admin)
                        .email(admin+"@gmail.com")
                        .username(admin)
                        .password(passwordEncoder.encode("000"))
                        .enabled(true)
                        .roles(List.of(ADMIN_ROLE))
                        .build()
                );
            }
            ListUtils.createCount(20, () -> faker.name().username()).stream().distinct().forEach(agent -> {
                agentRepository.save(Agent.builder()
                        .name(agent)
                        .email(agent+"@gmail.com")
                        .username(agent.replace(" ","_"))
                        .password(passwordEncoder.encode("000"))
                        .enabled(true)
                        .build()
                );
            });
        }
    }
    private Client fakeClient(String name)
    {
        return Client.builder()
                .name(name)
                .address(faker.address().fullAddress())
                .build();
    }
    private List<Contact> fakeContacts(int count)
    {
        List<Contact> contacts = new ArrayList<>();
        for(int i = 0; i < count; i++)
        {
            contacts.add(fakeContact());
        }
        return contacts;
    }

    private Contact fakeContact()
    {
        return Contact.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .build();
    }
    private List<Contact> fakeContacts(int count, Client c)
    {
        List<Contact> contacts = new ArrayList<>();
        for(int i = 0; i < count; i++)
        {
            contacts.add(fakeContact(c));
        }
        return contacts;
    }

    private Contact fakeContact(Client c)
    {
        return Contact.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .client(c)
                .build();
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
        activityStates.add(ActivityState.builder().activity(zapa).name("Annulé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(zapa).name("Retiré").Final(true).build());
        activityStates.add(ActivityState.builder().activity(zapa).name("Terminé").Final(true).build());
        zapa.setStates(activityStates);


        controle.getSituations().forEach(x -> x.setTask(controle));
        controle.getStates().forEach(x -> x.setTask(controle));
        zapa.getTasks().add(etdue);
        zapa.getTasks().add(controle);
        zapa.getTasks().add(PreparatrionLivraison);
        var fields = new ArrayList<ActivityField>();
        fields.add(ActivityField.builder().fieldName("CEM").fieldType(FieldType.String).activity(zapa).build());
        fields.add(ActivityField.builder().fieldName("NBre EL BE").fieldType(FieldType.String).activity(zapa).build());
        fields.add(ActivityField.builder().fieldName("NBRE EL Client").fieldType(FieldType.String).activity(zapa).build());
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
        activityStates.add(ActivityState.builder().activity(fi).name("Annulé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(fi).name("Retiré").Final(true).build());
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
    private void createIPONActivity() {
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
        activityStates.add(ActivityState.builder().activity(ipon).name("Annulé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(ipon).name("Retiré").Final(true).build());
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
        var control = Task.builder().name("PIQUETAGE").situations(taskSituationsControle).activity(piquetage).states(states).build();
        var verificationStates = new ArrayList();
        verificationStates.add(TaskState.builder().name("Valide").build());
        verificationStates.add(TaskState.builder().name("Non Valide").build());
        var verification = Task.builder().name("VÉRIFICATION DE RETOUR ").situations(taskSituationsverification).activity(piquetage).states(verificationStates).build();

        var activityStates = new ArrayList();
        activityStates.add(ActivityState.builder().activity(piquetage).name("En cours").initial(true).build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("PREFIBRÉ").build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("Terminé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("PRISE DE RDV PIQUETAGE").build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("PIQUETÉ NON REÇU").build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("Annulé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("Retiré").Final(true).build());    piquetage.setStates(activityStates);

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
        var etdueComac = Task.builder().name("Etude").situations(taskSituationsEtdueComac).activity(cdc).build();
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
        activityStates.add(ActivityState.builder().activity(cdc).name("Annulé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(cdc).name("Retiré").Final(true).build());
        cdc.setStates(activityStates);


        var groupFieldsCOMAC=ActivityFieldGroup.builder().name("COMAC").build();
        var fields = new ArrayList<ActivityField>();
        fields.add(ActivityField.builder().fieldName("Nombre  des Artères").fieldType(FieldType.String).group(groupFieldsCOMAC).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre  des appuis").fieldType(FieldType.String).group(groupFieldsCOMAC).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre d’Appuis implanter").fieldType(FieldType.String).group(groupFieldsCOMAC).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre de CRIT").fieldType(FieldType.String).activity(cdc).group(groupFieldsCOMAC).build());
        fields.add(ActivityField.builder().fieldName("Nombre d’Appuis à remplacer").fieldType(FieldType.String).activity(cdc).group(groupFieldsCOMAC).build());
        var groupFieldsCAPFT=ActivityFieldGroup.builder().name("CAPFT").build();

        fields.add(ActivityField.builder().fieldName("Nombre  des Artères").fieldType(FieldType.String).group(groupFieldsCAPFT).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre  des appuis").fieldType(FieldType.String).group(groupFieldsCAPFT).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre d’Appuis implanter").fieldType(FieldType.String).group(groupFieldsCAPFT).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre de CRIT").fieldType(FieldType.String).activity(cdc).group(groupFieldsCAPFT).build());
        cdc.setFields(fields);
        activityRepository.save(cdc);
    }
}
