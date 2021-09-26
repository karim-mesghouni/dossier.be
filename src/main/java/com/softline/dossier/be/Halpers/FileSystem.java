package com.softline.dossier.be.Halpers;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class FileSystem {
    private static Path attachmentsPath;

    public FileSystem(EnvUtil env) throws IOException {
        if (attachmentsPath == null) {
            attachmentsPath = env.getStoragePath().resolve("attachments");
            if (!attachmentsPath.toFile().exists()) {
                if (!attachmentsPath.toFile().mkdirs()) {
                    throw new IOException("could not create attachments storage");
                }
            }
        }
    }

    public static String randomMD5() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        StringBuffer hexString = new StringBuffer();
        byte[] data = md.digest(RandomStringUtils.randomAlphabetic(10).getBytes());
        for (int i = 0; i < data.length; i++) {
            hexString.append(Integer.toHexString((data[i] >> 4) & 0x0F));
            hexString.append(Integer.toHexString(data[i] & 0x0F));
        }
        return hexString.toString();
    }

    public Path getAttachmentsPath() {
        return attachmentsPath;
    }
}
