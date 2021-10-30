package com.softline.dossier.be.db;

import com.github.javafaker.Faker;
import com.softline.dossier.be.Halpers.ListUtils;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.repository.*;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Role;
import com.softline.dossier.be.security.repository.AgentRepository;
import com.softline.dossier.be.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class DbInitializer implements ApplicationRunner {
    private final ActivityRepository activityRepository;
    private final FileStateTypeRepository fileStateTypeRepository;
    private final BlockingLockingAddressRepository blockingLockingAddressRepository;
    private final BlockingQualificationRepository blockingQualificationRepository;
    private final BlockingLabelRepository blockingLabelRepository;
    private final ClientRepository clientRepository;
    private final CommuneRepository communeRepository;
    private final AgentRepository agentRepository;
    private final ReturnedCauseRepository returnedCauseRepository;
    private final ActivityStateRepository activityStateRepository;
    private final ContactRepository contactRepository;
    private final RoleRepository roleRepository;
    private final FileRepository fileRepository;
    private final FileTaskRepository fileTaskRepository;
    private final FileStateRepository fileStateRepository;
    private final TaskRepository taskRepository;
    private final TaskStateRepository taskStateRepository;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker; // used to generate fake(mock) date

    Activity zapa;
    Activity fi;
    Activity ipon;
    Activity piquetage;
    Activity cdc;

    @Transactional
    public void run(ApplicationArguments args) {
        if (activityRepository.count() == 0) {
            createZapaActivity();
            createFIActivity();
            createIPONActivity();
            createPiquetageActivity();
            createCDCActivity();
        }
        if (clientRepository.count() == 0) {
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("RH"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("AXIANS"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("AXIANS IDF"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("COVAGE"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("CPCP ROGNAC"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("CPCP SUD"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("FREE"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("NET DESIGNER"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("NET GEO"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("OPT"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("OPTTICOM"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("S30"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("SCOPELEC"))));
            contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient("SPIE"))));
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
            fileStateTypeRepository.save(FileStateType.builder().state("À RETIRER").build());
            fileStateTypeRepository.save(FileStateType.builder().state("STANDBY CLIENT").build());
            fileStateTypeRepository.save(FileStateType.builder().state("MANQUANT").build());
            fileStateTypeRepository.save(FileStateType.builder().state("REPRISE PIQUETAGE").build());
            fileStateTypeRepository.save(FileStateType.builder().state("NON AFFECTÉ").initial(true).build());
            fileStateTypeRepository.save(FileStateType.builder().state("REPRISE EN COURS D'ETUDE").build());
            fileStateTypeRepository.save(FileStateType.builder().state("KIZÉO NON ATTRIBUÉ").build());
            fileStateTypeRepository.save(FileStateType.builder().state("ANNULÉ").Final(true).build());
        }
        if (agentRepository.count() == 0) {
            final Role MANAGER = roleRepository.save(Role.builder().name("MANAGER").displayName("Administrateur").build());
            final Role REFERENT = roleRepository.save(Role.builder().name("REFERENT").displayName("Référent").build());
            final Role VALIDATOR = roleRepository.save(Role.builder().name("VALIDATOR").displayName("Valideur").build());
            final Role ACCOUNTANT = roleRepository.save(Role.builder().name("ACCOUNTANT").displayName("Chargé d'étude").build());
            List<Role> roles = List.of(MANAGER, REFERENT, VALIDATOR, ACCOUNTANT);
            for (var role : roles) {
                agentRepository.save(Agent.builder()
                        .name(faker.name().fullName())
                        .email(faker.internet().emailAddress())
                        .username(role.getName().toLowerCase(Locale.ROOT))
                        .password(passwordEncoder.encode("000"))
                        .enabled(true)
                        .activity(role.getName().equals("MANAGER") ? null : getOne(activityRepository.findAll()))
                        .role(role)
                        .build()
                );
            }
            ListUtils.createCount(20, () ->
                    agentRepository.save(Agent.builder()
                            .name(faker.name().fullName())
                            .email(faker.internet().emailAddress())
                            .username(faker.name().username())
                            .password(passwordEncoder.encode("000"))
                            .activity(getOne(activityRepository.findAll()))
                            .role(getOne(roles))
                            .enabled(true)
                            .build()
                    )
            );
        }
        if (blockingLabelRepository.count() == 0) {
            for (var name : List.of("AUTRE: BLOCAGE INTERNE",
                    "AUTRE BLOCAGE : GESTOT",
                    "IPON.SST : BLOCAGE IPON",
                    "CRIT: IMPLANTATION APPUIS",
                    "AUTRE: BLOCAGE PARTENAIRE",
                    "GFI.SST : BLOCAGE GEOFIBRE",
                    "NEGO: VERIF NBRE EL, IDF FIS",
                    "NUM POT DOC : DEMANDE DE N° APPUI",
                    "CAP FT : ETUDE FT À FAIRE OU ENCOURS",
                    "ENEDIS : ETUDE ENEDIS À FAIRE OU ENCOURS",
                    "DDE DESAT: DÉSATURATION OU DE MODIF.DE ZONE",
                    "BLOC.CMS : CRÉATION, MODIFICATION,SUPPRESSION CMS")) {
                blockingLabelRepository.save(BlockingLabel.builder().name(name).build());
            }
        }
        if (blockingQualificationRepository.count() == 0) {
            for (var name : List.of("CMS",
                    "PIT",
                    "FLUX",
                    "NEGO",
                    "AUTRE",
                    "CAP FT",
                    "CONNEXION",
                    "PIQUETAGE",
                    "PIT + CMS",
                    "PIT + NEGO",
                    "EN ATT CPCP",
                    "DESATURATION",
                    "CMS+MODIF ZE",
                    "EN ATT ORANGE",
                    "NEGO+MODIF ZE",
                    "NUMERO D'APPUI",
                    "PIT + MODIF ZE",
                    "MODIFICATION ZE",
                    "DESATURATION + CMS",
                    "CONNEXION +MODIF ZE",
                    "SOUS DIMENSIONNEMENT",
                    "SYNDIC NON IDENTIFIE",
                    "MODIFICATION NBRE EL",
                    "CMS+ SYNDIC NON IDENTIFIE")) {
                blockingQualificationRepository.save(BlockingQualification.builder().name(name).build());
            }
        }
        if (blockingLockingAddressRepository.count() == 0) {
            for (var name : List.of("NEGO",
                    "INTERNE",
                    "CMS+NEGO",
                    "SUPPORT BE",
                    "CMS+PILOTAGE",
                    "NEGO+PILOTAGE",
                    "SUPPORT BE+CMS",
                    "SUPPORT BE+NEGO",
                    "SUPPORT BE+PILOTAGE",
                    "PILOTAGE PARTENAIRE",
                    "PILOTAGE PARTENAIRES: SAMY")) {
                blockingLockingAddressRepository.save(BlockingLockingAddress.builder().address(name).build());
            }
        }

        if (fileRepository.count() == 0) {

            List<Client> clientList = clientRepository.findAll();
            List<Commune> cities = communeRepository.findAll();
            var agents = agentRepository.findAll();
            List<FileStateType> fileStateTypes = fileStateTypeRepository.findAll();
            var activities = activityRepository.findAll();
            var tasks = taskRepository.findAll();
            var taskStateList = taskStateRepository.findAll();
            var now = LocalDate.now();
            var files = new ArrayList<File>();
            var activityStates = activityStateRepository.findAll();
            var blockingLabels = blockingLabelRepository.findAll();
            var blockingQualifications = blockingQualificationRepository.findAll();
            var blockingLocks = blockingLockingAddressRepository.findAll();
            for (int i = 0; i < 30; i++) {
                var file = File.builder()
                        .client(getOne(clientList))
                        .order(files.size() + 1)
                        .agent(getOne(agents))
                        .commune(getOne(cities))
                        .createdDate(toDate(now))
                        .attributionDate(toLocalDate(faker.date().between(toDate(now), toDate(now.plusDays(20)))))
                        .returnDeadline(toLocalDate(faker.date().between(toDate(now.plusDays(21)), toDate(now.plusDays(40)))))
                        .provisionalDeliveryDate(toLocalDate(faker.date().between(toDate(now.plusDays(21)), toDate(now.plusDays(40)))))
                        .deliveryDate(toLocalDate(faker.date().between(toDate(now.plusDays(21)), toDate(now.plusDays(40)))))
                        .project(faker.app().name())
                        .build();
                var baseActivity = getOne(activities);
                var order = 0;
                var fileStates = new ArrayList<FileState>(Collections.singletonList(FileState.builder()
                        .file(file)
                        .agent(getOne(agents))
                        .type(fileStateTypeRepository.findFirstByInitialIsTrue())
                        .build()));
                fileStates.addAll(ListUtils.createCount(faker.number().numberBetween(0, 12),
                        () -> FileState.builder().file(file).agent(getOne(agents)).type(getOne(fileStateTypes)).build()));
                getOne(fileStates).setCurrent(true);
                file.setFileStates(fileStates);
                file.setFileDocs(new ArrayList<>(ListUtils.createCount(faker.number().numberBetween(0, 4),
                        () -> FileDoc.builder().file(file).path(faker.file().fileName()).description(faker.file().fileName()).agent(getOne(agents)).build())));
                AtomicInteger activityOrder = new AtomicInteger();
                List<FileActivity> fileActivities = new ArrayList<>(List.of(FileActivity.builder()
                        .activity(baseActivity)
                        .state(activityStateRepository.findFirstByInitialIsTrueAndActivity_Id(baseActivity.getId()))
                        .current(true)
                        .agent(getOne(agents))
                        .file(file)
                        .order(activityOrder.getAndIncrement()).build()));

                fileActivities.addAll(ListUtils.createCount(faker.number().numberBetween(0, 4), () ->
                                FileActivity.builder()
                                        .activity(getOne(activities))
                                        .state(getOne(activityStates))
                                        .agent(getOne(agents))
                                        .current(false)
                                        .file(file)
                                        .order(activityOrder.getAndIncrement())
                                        .build()
                        )
                );
                fileActivities.forEach(activity ->
                {
                    AtomicLong fileTaskOrder = new AtomicLong(1);
                    activity.setFileTasks(new ArrayList<>(ListUtils.createCount(faker.number().numberBetween(0, 6), () ->
                    {
                        var createdDate = faker.date().between(toDate(now), toDate(now.plusDays(20)));
                        var created = toLocalDate(createdDate);
                        FileTask task = FileTask.builder()
                                .fileActivity(activity)
                                .agent(getOne(agents))
                                .order(fileTaskOrder.getAndIncrement())
                                .assignedTo(getOne(agents))
                                .reporter(getOne(agents))
                                .task(getOne(tasks))
                                .state(getOne(taskStateList))
                                .createdDate(toDate(created))
                                .dueDate(toLocalDate(futureDaysFrom(createdDate, 2, 4)).atStartOfDay())
                                .endDate(toLocalDate(futureDaysFrom(createdDate, 5, 15)).atStartOfDay())
                                .title(faker.job().title())
                                .build();
                        var situations = task.getTask().getSituations();
                        var blocks = new ArrayList<FileTaskSituation>();
                        AtomicReference<Date> blockDate = new AtomicReference<>(futureDaysFrom(task.getCreatedDate(), 0, 15));
                        AtomicReference<Date> stateDate = new AtomicReference<>(futureDaysFrom(task.getCreatedDate(), 0, 15));
                        List<FileTaskSituation> thisTaskSituations = ListUtils.createCount(faker.number().numberBetween(1, 6), () ->
                        {
                            var situation = getOne(situations);
                            var fileTaskSituation = FileTaskSituation.builder()
                                    .situation(situation)
                                    .agent(getOne(agents))
                                    .fileTask(task)
                                    .createdDate(stateDate.get())
                                    .build();
                            if (situation.isBlock()) {
                                var block = Blocking
                                        .builder()
                                        .state(fileTaskSituation)
                                        .label(getOne(blockingLabels))
                                        .agent(getOne(agents))
                                        .lockingAddress(getOne(blockingLocks))
                                        .qualification(getOne(blockingQualifications))
                                        .createdDate(blockDate.get())
                                        .explication(faker.lebowski().quote())
                                        .build();
                                fileTaskSituation.setBlocking(block);
                                if (blocks.size() > 0) {
                                    blocks.stream().reduce((first, second) -> second).get().getBlocking().setDateUnBlocked(toLocalDate(blockDate.get()).atStartOfDay());
                                }
                                blocks.add(fileTaskSituation);
                                blockDate.set(futureDaysFrom(task.getCreatedDate(), 0, 15));
                            }
                            stateDate.set(futureDaysFrom(stateDate.get(), 0, 15));
                            return fileTaskSituation;
                        });
                        thisTaskSituations.stream().reduce((__, last) -> last).get().setCurrent(true);
                        task.setFileTaskSituations(thisTaskSituations);
                        return task;
                    })));
                    fakeDataFields(activity).getDataFields().forEach(field -> field.setAgent(getOne(agents)));
                });
                file.setBaseActivity(baseActivity);
                file.setFileActivities(fileActivities);
                if (faker.number().numberBetween(1, 10) == 1 && files.size() > 0) // 1 of 10 chance
                {
                    var toReprise = getOne(files);
                    file.setReprise(toReprise);
                }
                files.add(fileRepository.save(file));
            }
        }
    }

    private Date futureDaysFrom(Date date, int min, int max) {
        return faker.date().between(toDate(toLocalDate(date).plusDays(min)), toDate(toLocalDate(date).plusDays(max)));
    }

    private FileActivity fakeDataFields(FileActivity activity) {
        List<ActivityDataField> fields = new ArrayList<>();
        activity.getActivity().getFields().forEach(field ->
        {
            Object data;
            switch (field.getFieldType()) {
                case Date:
                    data = faker.date().future(10, TimeUnit.DAYS);
                    break;
                case Number:
                    data = faker.number().numberBetween(1, 10);
                    break;
                case String:
                    data = faker.book().author();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + field.getFieldType());
            }
            fields.add(ActivityDataField.builder()
                    .fieldName(field.getFieldName())
                    .fieldType(field.getFieldType())
                    .data(data.toString())
                    .fileActivity(activity)
                    .build());
        });
        activity.setDataFields(fields);
        return activity;
    }

    private Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }


    private <E> E getOne(List<E> items) {
        if (items == null || items.size() == 0) {
            return null;
        }
        return items.get(faker.number().numberBetween(0, items.size()));
    }

    private Client fakeClient(String name) {
        return Client.builder()
                .name(name)
                .address(faker.address().fullAddress())
                .build();
    }

    private Contact fakeContact() {
        return Contact.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .build();
    }

    private List<Contact> fakeContacts(Client c) {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            contacts.add(fakeContact(c));
        }
        return contacts;
    }

    private Contact fakeContact(Client c) {
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
        taskSituationsEtude.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var taskSituationsPreparatrionLivraison = new ArrayList<TaskSituation>();
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("En cours").build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Annulé").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var PreparatrionLivraison = Task.builder().name("Préparatrion de livraison").situations(taskSituationsPreparatrionLivraison).activity(zapa).build();
        PreparatrionLivraison.getSituations().forEach(x -> x.setTask(PreparatrionLivraison));
        var states = new ArrayList();
        states.add(TaskState.builder().name("Valide").build());
        states.add(TaskState.builder().name("Non valide").build());
        var etdue = Task.builder().name("Etude").situations(taskSituationsEtude).activity(zapa).build();
        var controle = Task.builder().name("Controle").situations(taskSituationsControle).activity(zapa).states(states).build();
        etdue.getSituations().forEach(x -> x.setTask(etdue));
        var activityStates = new ArrayList();
        activityStates.add(ActivityState.builder().activity(zapa).name("En cours").initial(true).build());
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
        fields.add(ActivityField.builder().fieldName("CEM").fieldType(FieldType.Number).activity(zapa).build());
        fields.add(ActivityField.builder().fieldName("NBre EL BE").fieldType(FieldType.Number).activity(zapa).build());
        fields.add(ActivityField.builder().fieldName("NBRE EL Client").fieldType(FieldType.Number).activity(zapa).build());
        fields.add(ActivityField.builder().fieldName("NBRE FOA").fieldType(FieldType.Number).activity(zapa).build());
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
        taskSituationsEtude.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var taskSituationsPreparatrionLivraison = new ArrayList<TaskSituation>();
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("En cours").build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Annulé").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var PreparatrionLivraison = Task.builder().name("Préparatrion de livraison").situations(taskSituationsPreparatrionLivraison).activity(fi).build();
        PreparatrionLivraison.getSituations().forEach(x -> x.setTask(PreparatrionLivraison));
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
        activityStates.add(ActivityState.builder().activity(fi).name("Annulé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(fi).name("Retiré").Final(true).build());
        activityStates.add(ActivityState.builder().activity(fi).name("Terminé").Final(true).build());
        fi.setStates(activityStates);
        fi.getTasks().add(etdue);
        fi.getTasks().add(controle);
        fi.getTasks().add(PreparatrionLivraison);
        var fields = new ArrayList<ActivityField>();
        fields.add(ActivityField.builder().fieldName("CEM").fieldType(FieldType.Number).activity(fi).build());
        fields.add(ActivityField.builder().fieldName("IMB").fieldType(FieldType.Number).activity(fi).build());
        fields.add(ActivityField.builder().fieldName("FIS").fieldType(FieldType.Number).activity(fi).build());
        fields.add(ActivityField.builder().fieldName("EL BE DL").fieldType(FieldType.Number).activity(fi).build());
        fields.add(ActivityField.builder().fieldName("EL IMB / FIS").fieldType(FieldType.Number).activity(fi).build());
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
        taskSituationsEtude.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var taskSituationsPreparatrionLivraison = new ArrayList<TaskSituation>();
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("En cours").build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Annulé").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var PreparatrionLivraison = Task.builder().name("Préparatrion de livraison").situations(taskSituationsPreparatrionLivraison).activity(ipon).build();
        PreparatrionLivraison.getSituations().forEach(x -> x.setTask(PreparatrionLivraison));
        var activityStates = new ArrayList();
        activityStates.add(ActivityState.builder().activity(ipon).name("En cours").initial(true).build());
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
        taskSituationsEtude.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var taskSituationsverification = new ArrayList<TaskSituation>();
        taskSituationsverification.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsverification.add(TaskSituation.builder().name("En cours").build());
        taskSituationsverification.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsverification.add(TaskSituation.builder().name("Annulé").Final(true).build());
        taskSituationsverification.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
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
        activityStates.add(ActivityState.builder().activity(piquetage).name("Terminé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("PRISE DE RDV PIQUETAGE").build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("PIQUETÉ NON REÇU").build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("Annulé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(piquetage).name("Retiré").Final(true).build());
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
        fields.add(ActivityField.builder().fieldName("NBRE APPUI PIQUETÉS").fieldType(FieldType.Number).activity(piquetage).activityBase(zapa).build());
        fields.add(ActivityField.builder().fieldName("IMB***").fieldType(FieldType.Number).activity(piquetage).activityBase(fi).build());
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
        taskSituationsEtdueComac.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var taskSituationsPreparatrionLivraison = new ArrayList<TaskSituation>();

        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("En cours").build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Annulé").Final(true).build());
        taskSituationsPreparatrionLivraison.add(TaskSituation.builder().name("Block").block(true).Final(true).build());
        var PreparatrionLivraison = Task.builder().name("Préparatrion de livraison").situations(taskSituationsPreparatrionLivraison).activity(cdc).build();
        PreparatrionLivraison.getSituations().forEach(x -> x.setTask(PreparatrionLivraison));
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
        activityStates.add(ActivityState.builder().activity(cdc).name("Terminé").Final(false).build());
        activityStates.add(ActivityState.builder().activity(cdc).name("Annulé").Final(true).build());
        activityStates.add(ActivityState.builder().activity(cdc).name("Retiré").Final(true).build());
        cdc.setStates(activityStates);


        var groupFieldsCOMAC = ActivityFieldGroup.builder().name("COMAC").build();
        var fields = new ArrayList<ActivityField>();
        fields.add(ActivityField.builder().fieldName("Nombre  des Artères").fieldType(FieldType.Number).group(groupFieldsCOMAC).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre  des appuis").fieldType(FieldType.Number).group(groupFieldsCOMAC).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre d’Appuis implanter").fieldType(FieldType.Number).group(groupFieldsCOMAC).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre de CRIT").fieldType(FieldType.Number).activity(cdc).group(groupFieldsCOMAC).build());
        fields.add(ActivityField.builder().fieldName("Nombre d’Appuis à remplacer").fieldType(FieldType.Number).activity(cdc).group(groupFieldsCOMAC).build());
        var groupFieldsCAPFT = ActivityFieldGroup.builder().name("CAPFT").build();
        fields.add(ActivityField.builder().fieldName("Nombre  des Artères").fieldType(FieldType.Number).group(groupFieldsCAPFT).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre  des appuis").fieldType(FieldType.Number).group(groupFieldsCAPFT).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre d’Appuis implanter").fieldType(FieldType.Number).group(groupFieldsCAPFT).activity(cdc).build());
        fields.add(ActivityField.builder().fieldName("Nombre de CRIT").fieldType(FieldType.Number).activity(cdc).group(groupFieldsCAPFT).build());
        cdc.setFields(fields);
        activityRepository.save(cdc);
    }
}
