package com.farmsync.FarmSyncBackend.repository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SqlQueryLoader {

    private final Map<String, String> queryCache = new ConcurrentHashMap<>();

    public String load(String fileName) {
        return queryCache.computeIfAbsent(fileName, name -> {
            try {
                ClassPathResource resource = new ClassPathResource("bigquery/" + name);
                return StreamUtils.copyToString(
                        resource.getInputStream(),
                        StandardCharsets.UTF_8
                );
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to load SQL file: " + name, exception);
            }
        });
    }
}