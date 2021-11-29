package com.softline.dossier.be.database.seed;

import com.github.javafaker.Faker;
import com.softline.dossier.be.Tools.ListUtils;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.repository.*;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.domain.Role;
import com.softline.dossier.be.security.repository.AgentRepository;
import com.softline.dossier.be.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.softline.dossier.be.Tools.DateHelpers.*;
import static com.softline.dossier.be.Tools.Functions.*;
import static com.softline.dossier.be.database.seed.SeederHelper.*;

@SuppressWarnings("DanglingJavadoc")
@RequiredArgsConstructor
@Component
@Slf4j(topic = "DBSeeder")
public class DatabaseSeeder implements ApplicationRunner {
    private final ActivityRepository activityRepository;
    private final FileStateTypeRepository fileStateTypeRepository;
    private final BlockingLockingAddressRepository blockingLockingAddressRepository;
    private final BlockingQualificationRepository blockingQualificationRepository;
    private final BlockingLabelRepository blockingLabelRepository;
    private final ClientRepository clientRepository;
    private final CommuneRepository communeRepository;
    private final AgentRepository agentRepository;
    private final ActivityStateRepository activityStateRepository;
    private final ContactRepository contactRepository;
    private final RoleRepository roleRepository;
    private final FileRepository fileRepository;
    private final TaskRepository taskRepository;
    private final TaskStateRepository taskStateRepository;
    private final PasswordEncoder passwordEncoder;
    /**
     * used to generate fake(mock) date
     *
     * @see FakerConfiguration#faker()
     */
    private final Faker faker;

    private final ResourceLoader resources;

    Activity zapa;
    Activity fi;
    Activity ipon;
    Activity piquetage;
    Activity cdc;

    @Value("${database.seeder.seed-files:false}")
    boolean seedFiles;
    @Value("${database.seeder.seed-agents:false}")
    boolean seedAgents;
    @Value("${database.seeder.seed-clients:false}")
    boolean seedClients;
    @Value("${database.seeder.seed-role-users:false}")
    boolean seedRoleUsers;
    @Value("${database.seeder.seed-communes:false}")
    boolean seedCommunes;

    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Waiting for database seeder...");
        if (activityRepository.count() == 0) {
            createZapaActivity();
            createFIActivity();
            createIPONActivity();
            createPiquetageActivity();
            createCDCActivity();
        }
        if (seedClients && clientRepository.count() == 0) {
            for (var cname : List.of("RH",
                    "AXIANS",
                    "AXIANS IDF",
                    "COVAGE",
                    "CPCP ROGNAC",
                    "CPCP SUD",
                    "FREE",
                    "NET DESIGNER",
                    "NET GEO",
                    "OPT",
                    "OPTTICOM",
                    "S30",
                    "SCOPELEC",
                    "SPIE")) {
                contactRepository.saveAll(fakeContacts(clientRepository.save(fakeClient(cname))));
            }
        }
        if (communeRepository.count() == 0 && seedCommunes) {
            createCommunes();
        }
        if (fileStateTypeRepository.count() == 0) {
            for (var fstname : List.of("Terminé", "RETIRÉ", "ANNULÉ")) {
                fileStateTypeRepository.save(FileStateType.builder().state(fstname).Final(true).build());
            }
            for (var fstname : List.of("NON AFFECTÉ")) {
                fileStateTypeRepository.save(FileStateType.builder().state(fstname).initial(true).build());
            }
            for (var fstname : List.of("En cours", "Livré", "À LIVRER",
                    "STANDBY", "À RETIRER", "STANDBY CLIENT", "MANQUANT",
                    "REPRISE PIQUETAGE", "REPRISE EN COURS D'ETUDE", "KIZÉO NON ATTRIBUÉ")) {
                fileStateTypeRepository.save(FileStateType.builder().state(fstname).build());
            }
        }
        if (agentRepository.count() == 0) {

            List<Role> roles = roleRepository.saveAll(List.of(
                    Role.builder().type(Role.Type.MANAGER).displayName("Administrateur").build(),
                    Role.builder().type(Role.Type.REFERENT).displayName("Référent").build(),
                    Role.builder().type(Role.Type.VALIDATOR).displayName("Valideur").build(),
                    Role.builder().type(Role.Type.ACCOUNTANT).displayName("Chargé d'étude").build())
            );
            for (var role : roles) {
                if (role.isAdmin() || seedRoleUsers) {
                    agentRepository.save(Agent.builder()
                            .name(role.getName().toLowerCase(Locale.ROOT))
                            .email(faker.internet().emailAddress())
                            .username(role.getName().toLowerCase(Locale.ROOT))
                            .password(passwordEncoder.encode("000"))
                            .enabled(true)
                            .activity(role.is(Role.Type.MANAGER) ? null : getOne(activityRepository.findAll()))
                            .role(role)
                            .build()
                    );
                }
            }
            if (seedAgents) {
                usersList().forEach(name ->
                        agentRepository.save(Agent.builder()
                                .name(name)
                                .email(faker.internet().emailAddress())
                                .username(faker.name().username())
                                .password(passwordEncoder.encode("000"))
                                .activity(getOne(activityRepository.findAll()))
                                .role(getOne(roles, r -> !r.is(Role.Type.MANAGER)))
                                .enabled(true)
                                .build()
                        )
                );
            }
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

        if (seedFiles && fileRepository.count() == 0) {
            List<Client> clientList = clientRepository.findAll();
            List<Commune> cities = communeRepository.findAll();
            var agents = agentRepository.findAll();
            List<FileStateType> fileStateTypes = fileStateTypeRepository.findAll();
            var activities = activityRepository.findAll();
            var tasks = taskRepository.findAll();
            var taskStateList = taskStateRepository.findAll();
            var now = LocalDateTime.now();
            var files = new ArrayList<File>();
            var activityStates = activityStateRepository.findAll();
            var blockingLabels = blockingLabelRepository.findAll();
            var blockingQualifications = blockingQualificationRepository.findAll();
            var blockingLocks = blockingLockingAddressRepository.findAll();
            var ord = new Object() {
                long fileTaskOrder = 0, fileActivityOrder = 0, fileOrder = 0, fileTaskNumber = 0;
            };
            ord.fileOrder = 0;
            for (int i = 0; i < 100; i++) {
                ord.fileTaskNumber = 0;
                var file = File.builder()
                        .client(getOne(clientList))
                        .order(++ord.fileOrder)
                        .agent(getOne(agents))
                        .commune(getOne(cities))
                        .createdDate(now)
                        .attributionDate(toLocalDate(faker.date().between(toDate(now), toDate(now.plusDays(20)))))
                        .returnDeadline(toLocalDate(faker.date().between(toDate(now.plusDays(21)), toDate(now.plusDays(40)))))
                        .provisionalDeliveryDate(toLocalDate(faker.date().between(toDate(now.plusDays(21)), toDate(now.plusDays(40)))))
                        .deliveryDate(toLocalDate(faker.date().between(toDate(now.plusDays(21)), toDate(now.plusDays(40)))))
                        .project(faker.app().name())
                        .build();
                var baseActivity = getOne(activities);
                var fileStates = new ArrayList<FileState>(Collections.singletonList(FileState.builder()
                        .file(file)
                        .agent(getOne(agents))
                        .type(fileStateTypeRepository.findFirstByInitialIsTrue())
                        .build()));
                fileStates.addAll(ListUtils.createCount(faker.number().numberBetween(0, 12),
                        () -> FileState.builder().file(file).agent(getOne(agents)).type(getOne(fileStateTypes)).build()));
                getOne(fileStates).setCurrent(true);
                file.setFileStates(fileStates);
                ord.fileActivityOrder = 0;
                List<FileActivity> fileActivities = new ArrayList<>(List.of(FileActivity.builder()
                        .activity(baseActivity)
                        .state(activityStateRepository.findFirstByInitialIsTrueAndActivity_Id(baseActivity.getId()))
                        .current(true)
                        .agent(getOne(agents, a -> safeValue(() -> a.getActivity().getId(), -1L) == baseActivity.getId()))
                        .file(file)
                        .order(++ord.fileActivityOrder).build()));
                runNTimes(faker.number().numberBetween(0, 7), () -> {
                    var act = getOne(activities);
                    if (fileActivities.stream().noneMatch(f -> f.getActivity().getId() == act.getId())) {
                        fileActivities.add(FileActivity.builder()
                                .activity(act)
                                .state(getOne(activityStates))
                                .agent(getOne(agents, a -> safeValue(() -> a.getActivity().getId(), -1L) == baseActivity.getId()))
                                .current(false)
                                .file(file)
                                .order(++ord.fileActivityOrder)
                                .build());
                    }
                });
                fileActivities.forEach(fileActivity ->
                {
                    fileActivity.setDocuments(ListUtils.createCount(faker.number().numberBetween(0, 4),
                            () -> Document.builder().fileActivity(fileActivity).path("\\\\" + faker.internet().privateIpV4Address() + "\\" + faker.file().fileName()).description(faker.file().fileName()).agent(getOne(agents)).build()));
                    ord.fileTaskOrder = 0;
                    fileActivity.setFileTasks(new ArrayList<>(ListUtils.createCount(faker.number().numberBetween(0, 6), () ->
                    {
                        var createdDate = faker.date().between(toDate(now), toDate(now.plusDays(20)));
                        var created = toLocalDateTime(createdDate);
                        var reporter = getOne(agents, a -> a.is(Role.Type.REFERENT) && a.getActivity().getId() == fileActivity.getActivity().getId(), () -> getOne(agents));
                        FileTask fileTask = FileTask.builder()
                                .fileActivity(fileActivity)
                                .agent(reporter)
                                .order(++ord.fileTaskOrder)
                                .number(++ord.fileTaskNumber)
                                .assignedTo(getOne(agents,
                                        a -> !a.isAdmin() && a.getActivity().getId() == fileActivity.getActivity().getId() && a.is(Role.Type.ACCOUNTANT),
                                        () -> getOne(agents)))
                                .reporter(reporter)
                                .task(getOne(fileActivity.getActivity().getTasks()))
                                .state(getOne(taskStateList))
                                .createdDate(created)
                                .startDate(toLocalDateTime(futureDaysFrom(createdDate, 0, 1)))
                                .dueDate(toLocalDateTime(futureDaysFrom(createdDate, 2, 4)))
                                .endDate(toLocalDateTime(futureDaysFrom(createdDate, 5, 15)))
                                .title(faker.job().title())
                                .build();
                        var situations = fileTask.getTask().getSituations();
                        var blocks = new ArrayList<FileTaskSituation>();
                        AtomicReference<LocalDateTime> blockDate = new AtomicReference<>(futureDaysFrom(fileTask.getCreatedDate(), 0, 15));
                        AtomicReference<LocalDateTime> stateDate = new AtomicReference<>(futureDaysFrom(fileTask.getCreatedDate(), 0, 15));
                        List<FileTaskSituation> thisTaskSituations = ListUtils.createCount(faker.number().numberBetween(1, 6), () ->
                        {
                            var situation = getOne(situations);
                            var fileTaskSituation = FileTaskSituation.builder()
                                    .situation(situation)
                                    .agent(getOne(agents))
                                    .fileTask(fileTask)
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
                                        .dateUnBlocked(LocalDateTime.now())
                                        .date(LocalDateTime.now())
                                        .explication(faker.lorem().sentence())
                                        .build();
                                fileTaskSituation.setBlocking(block);
                                if (blocks.size() > 0) {
                                    blocks.stream().reduce((first, second) -> second).get().getBlocking().setDateUnBlocked(blockDate.get());
                                }
                                blocks.add(fileTaskSituation);
                                blockDate.set(futureDaysFrom(fileTask.getCreatedDate(), 0, 15));
                            }
                            stateDate.set(futureDaysFrom(stateDate.get(), 0, 15));
                            return fileTaskSituation;
                        });
                        // set last situation as current
                        var state = thisTaskSituations.get(thisTaskSituations.size() - 1);
                        state.setCurrent(true);
                        safeRun(() -> state.getBlocking().setDateUnBlocked(null));
                        fileTask.setFileTaskSituations(thisTaskSituations);
                        return fileTask;
                    })));
                    fakeDataFields(fileActivity).getDataFields().forEach(field -> field.setAgent(getOne(agents)));
                });
                file.setBaseActivity(baseActivity);
                file.setFileActivities(fileActivities);
                if (faker.number().numberBetween(1, 10) == 1 && files.size() > 0) {
                    var toReprise = getOne(files);
                    file.setReprise(toReprise);
                }
                file.setNextFileTaskNumber(ord.fileTaskNumber + 1);
                files.add(fileRepository.save(file));
            }
        }
        log.info("database seeder finished");
    }


    private void createCommunes() {
        var xlsxResource = resources.getResource("classpath:communes.xlsx");
        var sheet = wrap(() -> new XSSFWorkbook(xlsxResource.getInputStream()))
                .getSheetAt(0);

        List<Commune> communes = new ArrayList<>();
        var errors = new HashMap<Integer, Throwable>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            try {
                var row = sheet.getRow(i);
                var commune = new Commune();
                commune.setPostalCode(StringUtils.leftPad("" + (int) row.getCell(0).getNumericCellValue(), 5, '0'));
                commune.setName(row.getCell(1).getStringCellValue());
                commune.setINSEECode(StringUtils.leftPad("" + (int) row.getCell(2).getNumericCellValue(), 5, '0'));
                commune.setDepartment(row.getCell(4).getStringCellValue());
                communes.add(commune);
            } catch (Throwable e) {
                errors.put(i, e);
            }
        }
        communeRepository.saveAll(communes);
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
        fields.add(ActivityField.builder().fieldName("CEM").fieldType(FieldType.String).activity(zapa).build());
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
        fields.add(ActivityField.builder().fieldName("CEM").fieldType(FieldType.String).activity(fi).build());
        fields.add(ActivityField.builder().fieldName("IMB").fieldType(FieldType.String).activity(fi).build());
        fields.add(ActivityField.builder().fieldName("FIS").fieldType(FieldType.String).activity(fi).build());
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
        fields.add(ActivityField.builder().fieldName("Poteaux FT").fieldType(FieldType.Number).activity(piquetage).activityBase(zapa).build());
        fields.add(ActivityField.builder().fieldName("Poteaux ERDF").fieldType(FieldType.Number).activity(piquetage).activityBase(zapa).build());
        fields.add(ActivityField.builder().fieldName("NBRE APPUI PIQUETÉS").fieldType(FieldType.Number).activity(piquetage).activityBase(zapa).build());
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
