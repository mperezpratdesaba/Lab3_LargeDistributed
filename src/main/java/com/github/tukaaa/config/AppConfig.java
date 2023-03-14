package com.github.tukaaa.config;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.Properties;

public class AppConfig implements Serializable {
    private final Integer sleepInterval;
    private final String mastodonServer;
    public final static AppConfig getConfig() {
        try (FileReader reader = new FileReader("app.conf")) {
            Properties properties = new Properties();
            properties.load(reader);
            Optional<AppConfig> maybeConfig =
                Optional.ofNullable(properties.getProperty("mastodon-server")).flatMap(server ->
                    Optional.ofNullable(properties.getProperty("sleep-interval-ms")).map(interval ->
                        new AppConfig(Integer.parseInt(interval), server)
                    )
            );
            return maybeConfig.orElseThrow(() -> new RuntimeException("Error processing configuration"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AppConfig(Integer sleepInterval, String mastodonServer) {
        this.sleepInterval = sleepInterval;
        this.mastodonServer = mastodonServer;
    }

    public Integer getSleepInterval() {
        return sleepInterval;
    }

    public String getMastodonServer() {
        return mastodonServer;
    }
}
