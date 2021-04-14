package com.softline.dossier.be.security.db;

import com.softline.dossier.be.security.domain.Agent;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class DbInitializer implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
     /*   if (agentRepository.count() == 0) {
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
        }*/

    }
}
