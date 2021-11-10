package com.softline.dossier.be.database;

import com.github.javafaker.Faker;
import com.softline.dossier.be.domain.ActivityDataField;
import com.softline.dossier.be.domain.Client;
import com.softline.dossier.be.domain.Contact;
import com.softline.dossier.be.domain.FileActivity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.softline.dossier.be.Tools.DateHelpers.*;
import static com.softline.dossier.be.Tools.Functions.safeValue;

/**
 * used by {@link DBSeeder} class
 */
@Slf4j(topic = "DBSeeder")
final class SeederHelper {
    private static final Faker faker = FakerConfiguration.faker();

    private SeederHelper() {
    }

    static Date futureDaysFrom(Date date, int min, int max) {
        return faker.date().between(toDate(toLocalDate(date).plusDays(min)), toDate(toLocalDate(date).plusDays(max)));
    }

    static LocalDateTime futureDaysFrom(LocalDateTime date, int min, int max) {
        return toLocalDateTime(faker.date().between(toDate(date.plusDays(min)), toDate(date.plusDays(max))));
    }

    static FileActivity fakeDataFields(FileActivity activity) {
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
                    .groupName(safeValue(() -> field.getGroup().getName()))
                    .build());
        });
        activity.setDataFields(fields);
        return activity;
    }

    /**
     * get one random element from the list
     *
     * @return one random item E from the list or null if the list is empty
     */
    static <E> E getOne(List<E> items) {
        if (items == null || items.size() == 0) {
            return null;
        }
        return items.get(faker.number().numberBetween(0, items.size()));
    }

    /**
     * get one random element from the list after applying the filter
     *
     * @return one random item E from the list or null if the filtered list is empty
     */
    static <E> E getOne(List<E> items, Predicate<E> filter) {
        if (items == null || items.size() == 0) {
            return null;
        }
        items = items.stream().filter(filter).collect(Collectors.toList());
        return items.get(faker.number().numberBetween(0, items.size()));
    }

    /**
     * get one random element from the list after applying the filter
     *
     * @return one random item E from the list, if the filtered list is empty then the return value of the fallback is returned
     */
    @SneakyThrows
    static <E> E getOne(List<E> items, Predicate<E> filter, Callable<E> fallback) {
        if (items == null || items.size() == 0) {
            return null;
        }
        items = items.stream().filter(filter).collect(Collectors.toList());
        if (items.size() == 0) {
            log.warn("getOne is using fallback");
            return fallback.call();
        }
        return items.get(faker.number().numberBetween(0, items.size()));
    }

    static Client fakeClient(String name) {
        return Client.builder()
                .name(name)
                .address(faker.address().fullAddress())
                .build();
    }

    static List<Contact> fakeContacts(Client c) {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            contacts.add(fakeContact(c));
        }
        return contacts;
    }

    static Contact fakeContact(Client c) {
        return Contact.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .client(c)
                .build();
    }

    /**
     * @return list of real names (original users names)
     */
    static List<String> usersList() {
        return List.of("ELTIFI Sana"
                , "HMAIDI Omar"
                , "BENNOUR Imen"
                , "AZIZI Chaima"
                , "HAJRI Khaoula"
                , "MABROUK Akrem"
                , "BEN AMOR Talel"
                , "KHEZAMI Aymen"
                , "TARHOUNI Donia"
                , "SININI Yosra"
                , "ELKEFI Salma"
                , "KOCHBATI Ep KAMOUN Nouha"
                , "AMDOUNI Med Ali"
                , "SASSI Olfa"
                , "AROUI Mahdi"
                , "LOUATI Ikbel"
                , "JAMAI Hiba"
                , "TOUZRI Jamil Aziz"
                , "SAID Mouhamed"
                , "BOULILA Fatma"
                , "DAKHLAOUI Rahma"
                , "BEN HLIMA Omar"
                , "NEMRI Ep. ELOUSGI Sarra"
                , "AGREBI Yosra"
                , "MELKI Maroua"
                , "MEJRI AFEF"
                , "BEN RACHED Oumayma"
                , "KAROUI Salim"
                , "BEJAOUI Nadia"
                , "AISSAOUI Mohamed Sofiene"
                , "OUESLATI Mariem"
                , "GUITOUNI Raoua"
                , "MASMOUDI Ines"
                , "HAMMAMI Aziza"
                , "HAJJI Tasnim"
                , "TAYEG Ghada"
                , "HOSNY Sawssen"
                , "BEN SALAH Ep. MOUSSA Mariem"
                , "CHAMMEM Manel"
                , "NEGUIA SalahEddine"
                , "DIOUANE Amor"
                , "GHEZALI Mahmoud"
                , "CHIHI Rihem"
                , "CHIHI Amal"
                , "BRAHMI Asma"
                , "LABIDI Khawla"
                , "BEN ELBEY Lobna"
                , "MAAROUFI Wissal"
                , "HAMMAMI Ines"
                , "KAABACHI Khaled"
                , "DAHMENI Ahmed"
                , "Cpcp"
                , "Nasri"
                , "Rafaa"
                , "Julien"
                , "Riahi Safa"
                , "Wael+Firas"
                , "Jelassi Wael"
                , "Hmaied Firas"
                , "Bennour Ramzi"
                , "Riahi Mohamed"
                , "Settou Mohamed"
                , "Belhaj Med Souhaieb"
                , "Hermi Ali"
                , "Mezni Emna"
                , "Sbai Malek"
                , "Abcha Amani"
                , "Aroui Mehdi"
                , "Lakti Marwa"
                , "Melki Marwa"
                , "Jlassi Wael"
                , "Souissi Beya"
                , "Senini Yosra"
                , "Ferchichi Aya"
                , "Khaldi Khawla"
                , "Touihri Nouha"
                , "Khaldi Yosra "
                , "Si Jemaa Akrem"
                , "Hannachi Fadwa"
                , "Karoui Mohamed"
                , "Riahi Mohamed "
                , "Hedidar Naouel"
                , "Nahali Nesrine"
                , "Mathlouthi Amel"
                , "Romdhani Chaima"
                , "Khelifi Ghassen"
                , "Bouhlel Oussema");
    }
}
