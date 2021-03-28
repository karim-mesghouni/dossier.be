package com.softline.dossier.be.db;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

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
    Activity zapa;
    Activity fi;
    Activity ipon;
    Activity piquetage;
    Activity cdc;
    @Autowired
    @Override
    public void run(ApplicationArguments args) throws Exception {

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
            communeRepository.save(Commune.builder().name("BOURG EN BRESSE").INSEECode("01053").postalCode("1000").build());
            communeRepository.save(Commune.builder().name("SAINT DENIS LES BOURG").INSEECode("01344").postalCode("1000").build());
            communeRepository.save(Commune.builder().name("BROU").INSEECode("01914").postalCode("1000").build());
            communeRepository.save(Commune.builder().name("AMAREINS").INSEECode("01003").postalCode("1090").build());
            communeRepository.save(Commune.builder().name("CESSEINS").INSEECode("01070").postalCode("1090").build());
            communeRepository.save(Commune.builder().name("AMAREINS FRANCHELEINS CES").INSEECode("01165").postalCode("1090").build());
            communeRepository.save(Commune.builder().name("GENOUILLEUX").INSEECode("01169").postalCode("1090").build());
            communeRepository.save(Commune.builder().name("GUEREINS").INSEECode("01183").postalCode("1090").build());
        }
        if (agentRepository.count() == 0) {
            agentRepository.save(Agent.builder().name("SETTOU Mohamed").build());
            agentRepository.save(Agent.builder().name("Bouhlel Oussema").build());
            agentRepository.save(Agent.builder().name("TOUZRI Jamil Aziz").build());
            agentRepository.save(Agent.builder().name("AMDOUNI Med Ali").build());
            agentRepository.save(Agent.builder().name("ELTIFI Sana").build());
            agentRepository.save(Agent.builder().name("SETTOU Mohamed").build());
            agentRepository.save(Agent.builder().name("HERMI Ali").build());
            agentRepository.save(Agent.builder().name("HMAIDI Omar").build());
            agentRepository.save(Agent.builder().name("BENNOUR Imen").build());
            agentRepository.save(Agent.builder().name("ABCHA Amani").build());
            agentRepository.save(Agent.builder().name("AZIZI Chaima").build());
            agentRepository.save(Agent.builder().name("HAJRI Khaoula").build());
            agentRepository.save(Agent.builder().name("MABROUK Akrem").build());
            agentRepository.save(Agent.builder().name("BEN AMOR Talel").build());
            agentRepository.save(Agent.builder().name("KHEZAMI Aymen").build());
            agentRepository.save(Agent.builder().name("BELHAJ MED Souhaieb").build());
            agentRepository.save(Agent.builder().name("TARHOUNI Donia").build());
            agentRepository.save(Agent.builder().name("SININI Yosra").build());
            agentRepository.save(Agent.builder().name("ELKEFI Salma").build());
            agentRepository.save(Agent.builder().name("KOCHBATI Ep KAMOUN Nouha").build());
            agentRepository.save(Agent.builder().name("AMDOUNI Med Ali").build());
            agentRepository.save(Agent.builder().name("SASSI Olfa").build());
            agentRepository.save(Agent.builder().name("AROUI Mahdi").build());
            agentRepository.save(Agent.builder().name("LOUATI Ikbel").build());
            agentRepository.save(Agent.builder().name("JAMAI Hiba").build());
            agentRepository.save(Agent.builder().name("TOUZRI Jamil Aziz").build());
            agentRepository.save(Agent.builder().name("SAID Mouhamed").build());
            agentRepository.save(Agent.builder().name("BOULILA Fatma").build());
            agentRepository.save(Agent.builder().name("DAKHLAOUI Rahma").build());
            agentRepository.save(Agent.builder().name("BEN HLIMA Omar").build());
            agentRepository.save(Agent.builder().name("NEMRI Ep. ELOUSGI Sarra").build());
            agentRepository.save(Agent.builder().name("AGREBI Yosra").build());
            agentRepository.save(Agent.builder().name("MELKI Maroua").build());
            agentRepository.save(Agent.builder().name("MEJRI AFEF").build());
            agentRepository.save(Agent.builder().name("BEN RACHED Oumayma").build());
            agentRepository.save(Agent.builder().name("KAROUI Salim").build());
            agentRepository.save(Agent.builder().name("BEJAOUI Nadia").build());
            agentRepository.save(Agent.builder().name("AISSAOUI Mohamed Sofiene").build());
            agentRepository.save(Agent.builder().name("OUESLATI Mariem").build());
            agentRepository.save(Agent.builder().name("GUITOUNI Raoua").build());
            agentRepository.save(Agent.builder().name("MASMOUDI Ines").build());
            agentRepository.save(Agent.builder().name("HAMMAMI Aziza").build());
            agentRepository.save(Agent.builder().name("HAJJI Tasnim").build());
            agentRepository.save(Agent.builder().name("TAYEG Ghada").build());
            agentRepository.save(Agent.builder().name("HOSNY Sawssen").build());
            agentRepository.save(Agent.builder().name("BEN SALAH Ep. MOUSSA Mariem").build());
            agentRepository.save(Agent.builder().name("CHAMMEM Manel").build());
            agentRepository.save(Agent.builder().name("NEGUIA SalahEddine").build());
            agentRepository.save(Agent.builder().name("DIOUANE Amor").build());
            agentRepository.save(Agent.builder().name("GHEZALI Mahmoud").build());
            agentRepository.save(Agent.builder().name("CHIHI Rihem").build());
            agentRepository.save(Agent.builder().name("CHIHI Amal").build());
            agentRepository.save(Agent.builder().name("BRAHMI Asma").build());
            agentRepository.save(Agent.builder().name("LABIDI Khawla").build());
            agentRepository.save(Agent.builder().name("BEN ELBEY Lobna").build());
            agentRepository.save(Agent.builder().name("MAAROUFI Wissal").build());
            agentRepository.save(Agent.builder().name("HAMMAMI Ines").build());
            agentRepository.save(Agent.builder().name("KAABACHI Khaled").build());
            agentRepository.save(Agent.builder().name("DAHMENI Ahmed").build());
            agentRepository.save(Agent.builder().name("Cpcp").build());
            agentRepository.save(Agent.builder().name("Nasri").build());
            agentRepository.save(Agent.builder().name("Rafaa").build());
            agentRepository.save(Agent.builder().name("Julien").build());
            agentRepository.save(Agent.builder().name("Riahi Safa").build());
            agentRepository.save(Agent.builder().name("Wael+Firas").build());
            agentRepository.save(Agent.builder().name("Hmaidi Omar").build());
            agentRepository.save(Agent.builder().name("Aroui Mehdi").build());
            agentRepository.save(Agent.builder().name("Jelassi Wael").build());
            agentRepository.save(Agent.builder().name("Hajji Tasnim").build());
            agentRepository.save(Agent.builder().name("Senini Yosra").build());
            agentRepository.save(Agent.builder().name("Agrebi Yosra").build());
            agentRepository.save(Agent.builder().name("Ferchichi Aya").build());
            agentRepository.save(Agent.builder().name("Hammami Aziza").build());
            agentRepository.save(Agent.builder().name("Khaldi Khawla").build());
            agentRepository.save(Agent.builder().name("Touihri Nouha").build());
            agentRepository.save(Agent.builder().name("Boulila Fatma").build());
            agentRepository.save(Agent.builder().name("Khezami Aymen").build());
            agentRepository.save(Agent.builder().name("Khaldi Yosra ").build());
            agentRepository.save(Agent.builder().name("Si Jemaa Akrem").build());
            agentRepository.save(Agent.builder().name("Hannachi Fadwa").build());
            agentRepository.save(Agent.builder().name("Labidi Khawla").build());
            agentRepository.save(Agent.builder().name("Karoui Mohamed").build());
            agentRepository.save(Agent.builder().name("Riahi Mohamed ").build());
            agentRepository.save(Agent.builder().name("Settou Mohamed").build());
            agentRepository.save(Agent.builder().name("Hedidar Naouel").build());
            agentRepository.save(Agent.builder().name("Guitouni Raoua").build());
            agentRepository.save(Agent.builder().name("Nahali Nesrine").build());
            agentRepository.save(Agent.builder().name("Ben Hlima Omar").build());
            agentRepository.save(Agent.builder().name("Mathlouthi Amel").build());
            agentRepository.save(Agent.builder().name("Khelifi Ghassen").build());
            agentRepository.save(Agent.builder().name("Bouhlel Oussema").build());
            agentRepository.save(Agent.builder().name("Romdhani Chaima").build());
        }
        if (fileStateTypeRepository.count() == 0) {
            fileStateTypeRepository.save(FileStateType.builder().state("Attribué").initial(true).build());
            fileStateTypeRepository.save(FileStateType.builder().state("LIVRÉ").Final(true).build());
            fileStateTypeRepository.save(FileStateType.builder().state("Standby par le client").build());
            fileStateTypeRepository.save(FileStateType.builder().state("À LIVRER").build());
            fileStateTypeRepository.save(FileStateType.builder().state("Annulé par le client").Final(true).build());

        }
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
        var states = new ArrayList();
        states.add(TaskState.builder().name("Valide").build());
        states.add(TaskState.builder().name("Non valide").build());
        var etdue = Task.builder().name("Etude").situations(taskSituationsEtude).activity(zapa).build();
        var controle = Task.builder().name("Controle").situations(taskSituationsControle).activity(zapa).states(states).build();
        etdue.getSituations().forEach(x -> x.setTask(etdue));
        controle.getSituations().forEach(x -> x.setTask(controle));
        controle.getStates().forEach(x -> x.setTask(controle));
        zapa.getTasks().add(etdue);
        zapa.getTasks().add(controle);
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
        var states = new ArrayList();
        states.add(TaskState.builder().name("Valide").build());
        states.add(TaskState.builder().name("Non valide").build());
        var etdue = Task.builder().name("Etude").situations(taskSituationsEtude).activity(fi).build();
        var controle = Task.builder().name("Controle").situations(taskSituationsControle).activity(fi).states(states).build();
        etdue.getSituations().forEach(x -> x.setTask(etdue));
        controle.getSituations().forEach(x -> x.setTask(controle));
        controle.getStates().forEach(x -> x.setTask(controle));
        fi.getTasks().add(etdue);
        fi.getTasks().add(controle);
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
        var taskSituationsCap = new ArrayList<TaskSituation>();
        taskSituationsCap.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsCap.add(TaskSituation.builder().name("En cours").build());
        taskSituationsCap.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsCap.add(TaskSituation.builder().name("Annulé").Final(true).build());
        var taskSituationsControle = new ArrayList<TaskSituation>();
        taskSituationsControle.add(TaskSituation.builder().name("A faire").initial(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("En cours").build());
        taskSituationsControle.add(TaskSituation.builder().name("Fait").Final(true).build());
        taskSituationsControle.add(TaskSituation.builder().name("Annulé").Final(true).build());

        var states = new ArrayList();
        states.add(TaskState.builder().name("Valide").build());
        states.add(TaskState.builder().name("Non valide").build());
        var etdueComac = Task.builder().name("Etdue COMAC").situations(taskSituationsEtdueComac).activity(cdc).build();
        var etdueCap = Task.builder().name("Etdue CAP-FT").situations(taskSituationsCap).activity(cdc).build();
        var controle = Task.builder().name("Controle").situations(taskSituationsControle).activity(cdc).states(states).build();
        etdueComac.getSituations().forEach(x -> x.setTask(etdueComac));
        etdueCap.getSituations().forEach(x -> x.setTask(etdueCap));
        controle.getSituations().forEach(x -> x.setTask(controle));
        controle.getStates().forEach(x -> x.setTask(controle));
        cdc.getTasks().add(etdueComac);
        cdc.getTasks().add(etdueCap);
        cdc.getTasks().add(controle);
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
