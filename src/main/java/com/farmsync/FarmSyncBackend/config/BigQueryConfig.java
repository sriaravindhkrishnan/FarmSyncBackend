package com.farmsync.FarmSyncBackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BigQueryConfig {

    @Bean
    public BigQuery bigQuery(@Value("${gcp.project-id}") String projectId) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        if (credentials == null) {
            throw new IllegalStateException("Google Cloud credentials are not configured");
        }
        System.out.println("BigQuery Project: " + projectId);
        System.out.println("Google Cloud credentials loaded successfully");
        return BigQueryOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }
}