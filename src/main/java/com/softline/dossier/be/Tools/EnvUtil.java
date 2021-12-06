package com.softline.dossier.be.Tools;

import com.softline.dossier.be.Application;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component
@Slf4j(topic = "EnvUtil")
public class EnvUtil {
    private static String url;
    private static Path storagePath;

    /**
     * @return server url without an ending slash ie: (http://www.server.com:8080)
     */
    @NotNull
    public static String getServerUrl() {
        if (url != null) return url;
        url = getVariable("server-real-url");
        return url;
    }

    @NotNull
    public static Path getStoragePath() {
        if (storagePath != null) return storagePath;
        storagePath = Paths.get(getVariable("filesystem.storage.absolute-path"));
        return storagePath;
    }

    @NotNull
    public static String getVariable(String name) throws NullPointerException {
        return Objects.requireNonNull(Application.context().getEnvironment().getProperty(name));
    }
}