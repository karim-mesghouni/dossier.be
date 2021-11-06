package com.softline.dossier.be.service;

import com.softline.dossier.be.Application;
import com.softline.dossier.be.Halpers.EnvUtil;
import com.softline.dossier.be.Halpers.FileSystem;
import com.softline.dossier.be.Halpers.Functions;
import com.softline.dossier.be.SSE.EventController;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.events.MessageEvent;
import com.softline.dossier.be.events.types.EntityEvent;
import com.softline.dossier.be.events.types.Event;
import com.softline.dossier.be.graphql.types.input.CommentInput;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
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


@Transactional
@Service
@RequiredArgsConstructor
public class CommentService extends IServiceBase<Comment, CommentInput, CommentRepository> {
    private final AgentRepository agentRepository;
    private final FileActivityRepository fileActivityRepository;
    private final FileTaskRepository fileTaskRepository;
    private final MessageRepository messageRepository;
    private final FileSystem fileSystem;

    public static void resolveCommentAttachments(@NotNull Comment comment, Pair<String, List<String>> changes) {
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

    private static String search(String pattern, String haystack) {
        Matcher m = Pattern.compile(pattern).matcher(haystack);
        if (m.find()) {
            return m.toMatchResult().group();
        }
        return "";
    }

    /**
     * replace any matched pattern in the subject with the return value of the callback
     *
     * @param pattern  the pattern finder
     * @param subject  the string to apply the pattern on
     * @param callback a function which will be supplied with each match result of the pattern in the subject, the match result will be replaced by the return value of this function
     */
    @NotNull
    private static String replace(Pattern pattern, Function<String, String> callback, CharSequence subject) {
        Matcher m = pattern.matcher(subject);
        StringBuilder newSubject = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(newSubject, callback.apply(m.toMatchResult().group()));
        }
        m.appendTail(newSubject);
        return newSubject.toString();
    }


    private static String resolveMentions(Comment comment) {
        return replace(Pattern.compile("(?<=\\{\"type\":\"mention\",\"attrs\":\\{\"id\":\")[^!]*?(?=\")"), agentId -> {
            Agent targetAgent = Agent.getByIdentifier(agentId);
            Message message = Message.builder().comment(comment).targetAgent(targetAgent).agent(Agent.thisAgent()).build();
            Application.context.getBean(MessageRepository.class).saveAndFlush(message);
            EventController.sendForUser(message.getTargetAgent().getId(), new MessageEvent(EntityEvent.Event.ADDED, message));
            // add "!" to indicate that the mention has been handled
            // so next time it will not be captured by the regex matcher (in the case where the comment was updated we won't re-create the Message again)
            return agentId + "!";
        }, comment.getContent());
    }

    @Override
    public List<Comment> getAll() {
        return repository.findAll();
    }

    public static void resolveCommentContent(Comment comment) {
        resolveCommentAttachments(comment, parseImageLinks(comment.getContent()));
        comment.setContent(resolveMentions(comment));
    }

    /**
     * parses the json and looks for any image links or base64 images and saves them locally
     *
     * @param json linted json string
     * @return the newJson string and the list of saved image names
     */
    public static Pair<String, List<String>> parseImageLinks(String json) {
        // NOTE: this regex is better but java has a limitation for repetition inside lookbehinds
        // REGEX: (?<=\{\"type\"\s*:\s*\"image\"\s*,.*\"src\"\s*:\s*\").*?(?=".*?\})
        // this one works only if the given json is linted (no new-lines and no empty spaces between keys and values)
        String pattern = "(?<=src\":\").*?(?=\")";
        List<String> imageNames = new ArrayList<>();
        String newJson = replace(Pattern.compile(pattern), src ->
        {
            if (src.startsWith("data:image/")) {
                String extension = search("(?<=data:image/).*(?=;base64,)", src);
                String base64 = search("(?<=;base64,).*", src);
                String name = saveImage(base64, extension);
                imageNames.add(name);
                return EnvUtil.getServerUrl() + "/attachments/" + name;
            } else {
                if (src.startsWith("http") && !src.startsWith(EnvUtil.getServerUrl())) {
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
                        Files.copy(is, FileSystem.getAttachmentsPath().resolve(name));
                        imageNames.add(name);
                        return EnvUtil.getServerUrl() + "/attachments/" + name;
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
     * save the base64 image locally with a random hashName
     *
     * @param base64    the image bytes in base64
     * @param extension the image extension without a dot
     * @return the saved image storage name with the extension
     */
    @Nullable
    private static String saveImage(String base64, String extension) {
        try {
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            String name = FileSystem.randomMD5() + "." + extension;
            var outputFile = FileSystem.getAttachmentsPath().resolve(name).toFile();
            ImageIO.write(image, extension, outputFile);
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Comment create(CommentInput input) throws IOException {
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
        Functions.safeRun(() -> comment.setFileTask(fileTaskRepository.findById(input.getFileTask().getId()).orElseThrow()));
        comment.setType(CommentType.Comment);
        resolveCommentAttachments(comment, parseImageLinks(input.getContent()));
        getRepository().save(comment);
        var currentAgent = agentRepository.findByUsername(((Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        EventController.sendForAllChannels(new Event<>("comment", comment));
        return comment;
    }

    @Override
    @SneakyThrows
    @PreAuthorize("hasPermission(#input.id, 'Comment', 'UPDATE_COMMENT')")
    public Comment update(CommentInput input) {
        var comment = repository.findWithAttachmentsById(input.getId());
        resolveCommentAttachments(comment, parseImageLinks(input.getContent()));
        repository.save(comment);
        return comment;
    }

    @Override
    public boolean delete(long id) {
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
    public Comment getById(long id) {
        return repository.findById(id).orElseThrow();
    }

    public String saveFile(DataFetchingEnvironment environment) throws IOException {
        var file = (ApplicationPart) environment.getArgument("image");
        var storageName = FileSystem.randomMD5() + "." + FilenameUtils.getExtension(file.getSubmittedFileName());
        Files.copy(file.getInputStream(), FileSystem.getAttachmentsPath().resolve(storageName));
//        attachFileRepository.save(AttachFile.builder()
//                .storageName(storageName)
//                .realName(file.getSubmittedFileName())
//                .contentType(file.getContentType())
//                .fileTask(fileTask)
//                .build())
        return EnvUtil.getServerUrl() + "/attachments/" + storageName;
    }

    public List<Comment> getAllCommentByFileId(Long fileId) {
        return getRepository().findAllByFileActivity_File_Id(fileId);
    }

    public Message getMessageByIdForThisAgent(long messageId) {
        return messageRepository.findByIdAndAgent_Id(messageId, Agent.thisAgent().getId());
    }

    public List<Message> getAllMessagesForThisAgent() {
        return messageRepository.findAllByAgent_IdOrderByCreatedDateDesc(Agent.thisAgent().getId());
    }
}