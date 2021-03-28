package com.softline.dossier.be.Halpers;

import com.softline.dossier.be.Application;
import com.softline.dossier.be.graphql.GQLExpetion;
import org.apache.catalina.core.ApplicationPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.function.Function;

import static java.nio.charset.Charset.defaultCharset;

public  class ImageHalper {

    public static String getFilename(Long personId, ApplicationPart file) throws NoSuchAlgorithmException {
        long epoch = Instant.now(Clock.systemDefaultZone()).toEpochMilli();
        return MessageFormat.format("{0}.{1}", hash(MessageFormat.format("{0}_{1}_{2}", personId,file.getSubmittedFileName(), epoch)), getType(file.getContentType()));
    }


    public static String getType(String mimetype) {
        MediaType mediaType = MediaType.parseMediaType(mimetype);
        if (!isImage(mediaType)) throw new GQLExpetion("Invalid content-type");
        else if (isJpeg(mediaType)) return "jpg";
        else return mediaType.getSubtype();
    }

    private static boolean isJpeg(MediaType mediaType) {
        return "jpeg".equalsIgnoreCase(mediaType.getSubtype());
    }

    private static boolean isImage(MediaType mediaType) {
        return "image".equalsIgnoreCase(mediaType.getType());
    }
    private static String hash(String identifier) throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA1").digest(identifier.getBytes(defaultCharset()));
        String result = "";
        for (byte digestByte : digest) {
            result = result + getHexadecimal(digestByte);
        }
        return result;
    }
    private static String getHexadecimal(byte digestByte) {
        return String.format("%02x", digestByte);
    }

}
