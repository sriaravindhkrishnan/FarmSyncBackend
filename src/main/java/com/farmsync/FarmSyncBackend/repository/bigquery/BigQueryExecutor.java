package com.farmsync.FarmSyncBackend.repository.bigquery;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
public class BigQueryExecutor {

    private final BigQuery bigQuery;
    private final String location;

    public BigQueryExecutor(BigQuery bigQuery,
                            @Value("${gcp.bigquery.location}") String location) {
        this.bigQuery = bigQuery;
        this.location = location;
    }

    /** For INSERT / UPDATE / DELETE, or when you want the raw result. */
    public TableResult execute(QueryJobConfiguration config) {
        try {
            JobId jobId = JobId.newBuilder()
                    .setLocation(location)
                    .setJob(UUID.randomUUID().toString())
                    .build();
            return bigQuery.query(config, jobId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("BigQuery execution was interrupted", e);
        }
    }

    /** For SELECT: runs the query and maps every row with the given mapper. */
    public <T> List<T> query(QueryJobConfiguration config, Function<FieldValueList, T> rowMapper) {
        TableResult result = execute(config);
        List<T> list = new ArrayList<>();
        for (FieldValueList row : result.iterateAll()) {
            list.add(rowMapper.apply(row));
        }
        return list;
    }
}