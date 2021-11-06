package com.softline.dossier.be.Halpers;

import com.softline.dossier.be.Application;
import lombok.SneakyThrows;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component
public class EnvUtil {
    private static Integer port;
    private static String hostname;
    private static Path storagePath;

    public static Integer getPort() {
        if (port == null) {
            Environment environment = Application.context.getEnvironment();
            port = Integer.valueOf(environment.getProperty("local.server.port"));
        }
        return port;
    }

    @SneakyThrows
    public static String getHostname() {
        // TODO ... would this cache cause issue, when network env change ???
        if (hostname == null) {
            hostname = InetAddress.getLocalHost().getHostAddress();
        }
        return hostname;
    }

    /**
     * @return server url without an ending slash ie: (http://server.com:8080)
     */
    public static String getServerUrl() {
        return "http://" + getHostname() + ":" + getPort();
    }

    public static Path getStoragePath() {
        if (storagePath == null) {
            Environment environment = Application.context.getEnvironment();
            storagePath = Paths.get(Objects.requireNonNull(environment.getProperty("filesystem.storage.absolute-path")));
        }
        return storagePath;
    }
}