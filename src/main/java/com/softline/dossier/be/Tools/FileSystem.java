package com.softline.dossier.be.Tools;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class FileSystem {
    private static Path attachmentsPath, assetsPath;

    public static String randomMD5() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        StringBuilder hexString = new StringBuilder();
        byte[] bytes = md.digest(RandomStringUtils.randomAlphabetic(10).getBytes());
        for (byte _byte : bytes) {
            hexString.append(Integer.toHexString((_byte >> 4) & 0x0F));
            hexString.append(Integer.toHexString(_byte & 0x0F));
        }
        return hexString.toString();
    }

    @SneakyThrows
    public static Path getAttachmentsPath() {
        if (attachmentsPath == null) {
            attachmentsPath = EnvUtil.getStoragePath().resolve("attachments");
            if (!attachmentsPath.toFile().exists()) {
                if (!attachmentsPath.toFile().mkdirs()) {
                    throw new IOException("could not create attachments storage");
                }
            }
        }
        return attachmentsPath;
    }

    @SneakyThrows
    public static Path getAssetsPath() {
        if (assetsPath == null) {
            assetsPath = EnvUtil.getStoragePath().resolve("assets");
            if (!assetsPath.toFile().exists()) {
                if (!assetsPath.toFile().mkdirs()) {
                    throw new IOException("could not create assets storage");
                }
            }
        }
        return assetsPath;
    }
}
