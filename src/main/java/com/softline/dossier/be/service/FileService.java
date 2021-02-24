package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.FileHistoryDTO;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Transactional
@Service
public class FileService extends IServiceBase<File, FileInput,FileRepository> {

    @Override
    public List<File> getAll() {
        return (List<File>) repository.findAll();
    }

    @Override
    public File create(FileInput input) {
        var file = File.builder()
                .project(input.getProject())
                .provisionalDeliveryDate(input.getProvisionalDeliveryDate())
                .attributionDate(input.getAttributionDate())
                .deliveryDate(input.getDeliveryDate())
                .returnDeadline(input.getReturnDeadline())
                .cem(input.getCem())
                .fileStates(new ArrayList<>())
                .client(Client.builder().id(input.getClient().getId()).build())
                .commune(Commune.builder().id(input.getCommune().getId()).build()).build();
        return repository.save(file);
    }

    @Override
    public File update(FileInput input) {
        return null;
    }

    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public File getById(long id) {
        return (File) repository.findById(id).orElseThrow();
    }

    public PageList<FileDTO> getAllFilePageFilter(FileFilterInput input) {
        var fileDtos = new ArrayList<FileDTO>();
        var result = getRepository().getByFilter(input);
        if (result.getValue1() != null && !result.getValue1().isEmpty())
            result.getValue1().forEach(f -> {
                if (f.getFileStates() != null & !f.getFileStates().isEmpty())
                    f.getFileStates().stream().forEach(a -> fileDtos.add(a.getActivity() != null ? FileDTO.builder()
                            .fileActivityId(a.getId())
                            .activityName(a.getActivity().getName())
                            .attributionDate(f.getAttributionDate())
                            .fileStateId(a.getId())
                            .cem(f.getCem())
                            .idFile(f.getId())
                            .project(f.getProject())
                            .deliveryDate(f.getDeliveryDate())
                            .returnDeadline(f.getReturnDeadline())
                            .provisionalDeliveryDate(f.getProvisionalDeliveryDate()).build() :
                            FileDTO.builder()
                                    .attributionDate(f.getAttributionDate())
                                    .cem(f.getCem())
                                    .idFile(f.getId())
                                    .fileStateId(a.getId())
                                    .project(f.getProject())
                                    .deliveryDate(f.getDeliveryDate())
                                    .returnDeadline(f.getReturnDeadline())
                                    .provisionalDeliveryDate(f.getProvisionalDeliveryDate()).build()
                    ));
                else
                    fileDtos.add(FileDTO.builder()
                            .attributionDate(f.getAttributionDate())
                            .cem(f.getCem())
                            .idFile(f.getId())
                            .project(f.getProject())
                            .deliveryDate(f.getDeliveryDate())
                            .returnDeadline(f.getReturnDeadline())
                            .provisionalDeliveryDate(f.getProvisionalDeliveryDate()).build());
            });
        return new PageList<>(fileDtos, result.getValue0());
    }
    public List<FileHistoryDTO> getFileHistory(long id){
        Comparator<FileHistoryDTO> dateComparator = new Comparator<FileHistoryDTO>() {
            @Override
            public int compare(FileHistoryDTO o1, FileHistoryDTO o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };
        AtomicInteger i= new AtomicInteger();
        var history=new ArrayList<FileHistoryDTO>();
         var file=   repository.findById(id).orElseThrow();
        history.add(FileHistoryDTO.builder().id(i.incrementAndGet())
                .who("Agent 1")
                .date(file.getCreatedDate())
                .message("create File").build());
        file.getFileStates().forEach(x->
        {
            var state=FileHistoryDTO.builder().id(i.incrementAndGet())
                    .who("Agent 1")
                    .date(x.getCreatedDate())
                    .message("change file to " + x.getActivity().getName()).build();
            state.setChildren(new ArrayList());
            x.getFilePhases().forEach(filePhase->{
                state.getChildren().add(FileHistoryDTO.builder().id(i.incrementAndGet())
                        .who("Agent 1")
                        .date(filePhase.getCreatedDate())
                        .message("change  filePhase "+x.getActivity().getName()+" to " + filePhase.getPhase().getName()).build());
                filePhase.getFilePhaseAgents().forEach(phaseAgent->{
                    phaseAgent.getFilePhaseStates().forEach(filePhaseState -> {
                        state.getChildren().add(FileHistoryDTO.builder().id(i.incrementAndGet())
                                .who("Agent 1")
                                .date(filePhaseState.getCreatedDate())
                                .message("change  file Phase "+filePhase.getPhase().getName()+" to " + filePhaseState.getState().getState()).build());
                        state.getChildren().sort(dateComparator);
                    });

                });
            });
            history.add(state);
         }
        );
        history.sort(dateComparator);
        return  history;

    }
}