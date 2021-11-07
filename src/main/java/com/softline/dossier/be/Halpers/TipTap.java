package com.softline.dossier.be.Halpers;

import com.softline.dossier.be.domain.Comment;
import com.softline.dossier.be.domain.CommentAttachment;
import com.softline.dossier.be.domain.Message;
import com.softline.dossier.be.security.domain.Agent;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import java.util.regex.Pattern;

import static com.softline.dossier.be.Halpers.TextHelper.search;

/**
 * a helper class which can parse the json content of the javascript library tiptap.js
 */
@Slf4j(topic = "TipTap-Helper")
public final class TipTap {
    private TipTap() {
    }

    /**
     * parses the json and looks for any image links or base64 images and saves them locally <br>
     * and then look for any mentioned agent and send him a message
     */
    public static void resolveCommentContent(@NotNull Comment comment) {
        resolveCommentAttachments(comment);
        comment.setContent(resolveMentions(comment));
    }

    public static void resolveCommentAttachments(@NotNull Comment comment) {
        Pair<String, List<String>> changes = parseImageLinks(comment.getContent());
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

    private static String resolveMentions(@NotNull Comment comment) {
        return TextHelper.replace(Pattern.compile("(?<=\\{\"type\":\"mention\",\"attrs\":\\{\"id\":\")[^!]*?(?=\")"), agentId -> {
            Agent targetAgent = Agent.getByIdentifier(agentId);
            Message message = Message.builder().comment(comment).targetAgent(targetAgent).agent(Agent.thisAgent()).build();
            comment.getMessages().add(message);
            // add "!" to indicate that the mention has been handled
            // so next time it will not be captured by the above regex matcher (in the case where the comment was updated we won't re-create the Message again)
            return agentId + "!";
        }, comment.getContent());
    }

    /**
     * parses the json and looks for any image links or base64 images and saves them locally
     *
     * @param json linted json string
     * @return the newJson string and the list of saved image names
     */
    private static Pair<String, List<String>> parseImageLinks(String json) {
        // NOTE: this regex is better but java has a limitation for repetition inside lookbehinds
        // REGEX: (?<=\{\"type\"\s*:\s*\"image\"\s*,.*\"src\"\s*:\s*\").*?(?=".*?\})
        // this one works only if the given json is linted (no new-lines and no empty spaces between keys and values)
        String pattern = "(?<=src\":\").*?(?=\")";
        List<String> imageNames = new ArrayList<>();
        String newJson = TextHelper.replace(Pattern.compile(pattern), src ->
        {
            if (src.startsWith("data:image/")) {
                String extension = search("(?<=data:image/).*(?=;base64,)", src);
                String base64 = search("(?<=;base64,).*", src);
                String name = save64Image(base64, extension);
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
                } else {
                    log.error("unknown image source ({}), when parsing comment content: {}", src, json);
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
    private static String save64Image(String base64, String extension) {
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
}
