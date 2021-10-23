package com.softline.dossier.be.service;

import com.softline.dossier.be.Halpers.EnvUtil;
import com.softline.dossier.be.Halpers.FileSystem;
import com.softline.dossier.be.Halpers.Functions;
import com.softline.dossier.be.SSE.Event;
import com.softline.dossier.be.SSE.EventController;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.NotifyMessageInput;
import com.softline.dossier.be.repository.CommentRepository;
import com.softline.dossier.be.repository.FileActivityRepository;
import com.softline.dossier.be.repository.FileTaskRepository;
import com.softline.dossier.be.repository.MessageRepository;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.repository.AgentRepository;
import graphql.schema.DataFetchingEnvironment;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Transactional
@Service
@RequiredArgsConstructor
public class CommentService extends IServiceBase<Comment, CommentInput, CommentRepository>
{
    private final AgentRepository agentRepository;
    private final FileActivityRepository fileActivityRepository;
    private final FileTaskRepository fileTaskRepository;
    private final MessageRepository messageRepository;
    private final FileSystem fileSystem;

    private static void resolveCommentAttachments(Comment comment, Pair<String, List<String>> changes)
    {
        var attachments = comment.getAttachments();
        for (var image : changes.getSecond()) {
            attachments.add(CommentAttachment.builder()
                    .comment(comment)
                    .contentType(URLConnection.guessContentTypeFromName(image))
                    .realName("comment image")
                    .storageName(image)
                    .build());
        }
        comment.setAttachments(attachments);
        comment.setContent(changes.getFirst());
    }

    private static String search(String pattern, String haystack)
    {
        Matcher m = Pattern.compile(pattern).matcher(haystack);
        if (m.find()) {
            return m.toMatchResult().group();
        }
        return "";
    }

    private static String replace(Pattern pattern, Function<String, String> callback, CharSequence subject)
    {
        Matcher m = pattern.matcher(subject);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(sb, callback.apply(m.toMatchResult().group()));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    @Override
    public List<Comment> getAll()
    {
        return repository.findAll();
    }

    @Override
    public Comment create(CommentInput input) throws IOException
    {
        var fileActivity = fileActivityRepository.findById(input.getFileActivity().getId()).orElseThrow();
        var agnet = agentRepository.findById(input.getAgent().getId()).orElseThrow();
        var comment = Comment.builder()
                .fileActivity(FileActivity.builder()
                        .id(fileActivity.getId())
                        .activity(Activity.builder().id(fileActivity.getActivity().getId()).name(fileActivity.getActivity().getName()).build())
                        .file(File.builder().id(fileActivity.getFile().getId()).project(fileActivity.getFile().getProject()).build())
                        .build()
                )
                .content(input.getContent())
                .agent(Agent.builder().name(agnet.getName()).id(agnet.getId()).build())
                .build();
        Functions.safeRun(() -> comment.setFileTask(fileTaskRepository.getOne(input.getFileTask().getId())));
        comment.setType(CommentType.Comment);
        Pair<String, List<String>> changes = parseImageLinks(input.getContent());
        resolveCommentAttachments(comment, changes);
        getRepository().save(comment);
        var currentAgent = agentRepository.findByUsername(((Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        EventController.sendForAllChannels(new Event("comment", comment));
        return comment;
    }

    @Override
    @SneakyThrows
    public Comment update(CommentInput input)
    {
        Pair<String, List<String>> changes = parseImageLinks(input.getContent());
        var comment = repository.findWithAttachmentsById(input.getId());
        resolveCommentAttachments(comment, changes);
        repository.save(comment);
        return comment;
    }

    private Pair<String, List<String>> parseImageLinks(String json)
    {
        // NOTE: this regex is better but java has a limitation for repetition inside lookbehinds
        // REGEX: (?<=\{\"type\"\s*:\s*\"image\"\s*,.*\"src\"\s*:\s*\").*?(?=".*?\})
        // this one works only if the given json is linted (no empty spaces between keys and values)
        String pattern = "(?<=src\":\").*?(?=\")";
        List<String> imageNames = new ArrayList<>();
        String newJson = replace(Pattern.compile(pattern), src ->
        {
            if (src.startsWith("data:image/")) {
                String extension = search("(?<=data:image/).*(?=;base64,)", src);
                String base64 = search("(?<=;base64,).*", src);
                String name = saveImage(base64, extension);
                imageNames.add(name);
                return EnvUtil.getInstance().getServerUrlPrefi() + "/attachments/" + name;
            } else {
                if (src.startsWith("http") && !src.startsWith(EnvUtil.getInstance().getServerUrlPrefi())) {
                    try {
                        URL url = new URL(src);
                        var is = url.openStream();
                        String extension = null;
                        ImageInputStream iis = ImageIO.createImageInputStream(is);
                        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                        if (readers.hasNext()) {
                            ImageReader reader = readers.next();
                            extension = reader.getFormatName().toLowerCase(Locale.ROOT);
                        }
                        if (extension == null || extension.isEmpty()) {
                            return src;
                        }
                        String name = FileSystem.randomMD5() + "." + extension;
                        is.close();
                        is = url.openStream();
                        Files.copy(is, fileSystem.getAttachmentsPath().resolve(name));
                        imageNames.add(name);
                        return EnvUtil.getInstance().getServerUrlPrefi() + "/attachments/" + name;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return src;
        }, json);
        return new Pair<>(newJson, imageNames);
    }

    /**
     * @return saved image storage name
     */
    @Nullable
    private String saveImage(String base64, String extension)
    {
        try {
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            String name = FileSystem.randomMD5() + "." + extension;
            var outputFile = fileSystem.getAttachmentsPath().resolve(name).toFile();
            ImageIO.write(image, extension, outputFile);
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(long id)
    {
        var comment = repository.findById(id).orElseThrow();
        if (comment.getFileTask() != null) {
            if (comment.getType() == CommentType.Returned) {
                comment.getFileTask().setRetour(null);
            } else {
                if (comment.getType() == CommentType.Description) {
                    comment.getFileTask().setDescription(null);
                }
            }
        }
        repository.deleteById(id);
        return true;
    }

    @Override
    public Comment getById(long id)
    {
        return repository.findById(id).orElseThrow();
    }

    public String saveFile(DataFetchingEnvironment environment) throws IOException
    {
        var file = (ApplicationPart) environment.getArgument("image");
        var storageName = FileSystem.randomMD5() + "." + FilenameUtils.getExtension(file.getSubmittedFileName());
        Files.copy(file.getInputStream(), fileSystem.getAttachmentsPath().resolve(storageName));
//        attachFileRepository.save(AttachFile.builder()
//                .storageName(storageName)
//                .realName(file.getSubmittedFileName())
//                .contentType(file.getContentType())
//                .fileTask(fileTask)
//                .build())
        return EnvUtil.getInstance().getServerUrlPrefi() + "/attachments/" + storageName;
    }

    public List<Comment> getAllCommentByFileId(Long fileId)
    {
        return getRepository().findAllByFileActivity_File_Id(fileId);
    }

    // TODO: optimize
    public boolean notifyMessage(NotifyMessageInput input)
    {
        if (input.getAgentIds() != null) {
            var comment = getRepository().findById(input.getIdComment()).orElseThrow();
            var messages = input.getAgentIds().stream().distinct().map(agentId ->
                    Message.builder()
                            .readMessage(false)
                            .comment(Comment.builder().id(comment.getId())
                                    .fileActivity(FileActivity.builder().file(File.builder().id(comment.getFileActivity().getFile().getId())
                                                    .project(comment.getFileActivity().getFile().getProject())
                                                    .build())
                                            .activity(Activity.builder().id(comment.getFileActivity().getActivity().getId())
                                                    .name(comment.getFileActivity().getActivity().getName())
                                                    .build()).build())
                                    .fileTask(comment.getFileTask() != null ? FileTask.builder().id(comment.getFileTask().getId()).order(comment.getFileTask().getOrder())
                                            .task(Task.builder().id(comment.getFileTask().getTask().getId()).name(comment.getFileTask().getTask().getName()).build()).build() : null)
                                    .agent(Agent.builder().id(comment.getAgent().getId()).name(comment.getAgent().getName()).build()).build()).
                            agent(Agent.builder().id(agentId).build()).build()
            ).collect(Collectors.toList());
            messageRepository.saveAll(messages);

            messages.forEach(x -> EventController.sendForUser(x.getAgent().getId(), Event.builder().name("message").payload(x).build()));
            return true;
        }
        return false;
    }

    public List<Message> getMessages(Long agentId)
    {
        return messageRepository.findAllByAgent_Id(agentId, Sort.by(Sort.Direction.DESC, Message_.CREATED_DATE));
    }
}