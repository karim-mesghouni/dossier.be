package com.softline.dossier.be.Halpers;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component
public class EnvUtil {
    private static EnvUtil instance;
    @Autowired
    Environment environment;
    private String port;
    private String hostname;

    public EnvUtil() {
        if (instance == null) {
            instance = this;
        }
    }

    public static EnvUtil getInstance() {
        return instance;
    }

    /**
     * Get port.
     *
     * @return
     */
    public String getPort() {
        if (port == null) {
            port = environment.getProperty("local.server.port");
        }
        return port;
    }

    /**
     * Get port, as Integer.
     *
     * @return
     */
    public Integer getPortAsInt() {
        return Integer.valueOf(getPort());
    }

    /**
     * Get hostname.
     *
     * @return
     */
    public String getHostname() throws UnknownHostException {
        // TODO ... would this cache cause issue, when network env change ???
        if (hostname == null) {
            hostname = InetAddress.getLocalHost().getHostAddress();
        }
        return hostname;
    }

    @SneakyThrows
    public String getServerUrlPrefi() {
        return "http://" + getHostname() + ":" + getPort();
    }

    public Path getStoragePath() {
        return Paths.get(Objects.requireNonNull(environment.getProperty("filesystem.storage.absolute-path")));
    }
}