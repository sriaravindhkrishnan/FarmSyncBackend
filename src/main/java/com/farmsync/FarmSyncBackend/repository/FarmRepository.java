package com.farmsync.FarmSyncBackend.repository;

import com.farmsync.FarmSyncBackend.model.FarmDetails;
import com.farmsync.FarmSyncBackend.repository.bigquery.BigQueryExecutor;
import com.farmsync.FarmSyncBackend.repository.bigquery.BigQueryTypes;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.QueryParameterValue;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.farmsync.FarmSyncBackend.repository.bigquery.BigQueryTypes.toInstant;

@Repository
public class FarmRepository {

    private final SqlQueryLoader sqlQueryLoader;
    private final BigQueryExecutor bigQueryExecutor;
    public FarmRepository(BigQueryExecutor bigQueryExecutor, SqlQueryLoader sqlQueryLoader) {
        this.bigQueryExecutor = bigQueryExecutor;
        this.sqlQueryLoader = sqlQueryLoader;
    }

    public boolean save(FarmDetails farmDetails) {
        try {
            String sql = sqlQueryLoader.load("insert_form_details.sql");
            QueryJobConfiguration config = QueryJobConfiguration.newBuilder(sql)
                    .setUseLegacySql(false)
                    .addNamedParameter("farmId", QueryParameterValue.string(farmDetails.farmId()))
                    .addNamedParameter("userId", QueryParameterValue.string(farmDetails.userId()))
                    .addNamedParameter("farmName", QueryParameterValue.string(farmDetails.farmName()))
                    .addNamedParameter("cropType", QueryParameterValue.string(farmDetails.cropType()))
                    .addNamedParameter("latitude", QueryParameterValue.float64(farmDetails.latitude()))
                    .addNamedParameter("longitude", QueryParameterValue.float64(farmDetails.longitude()))
                    .addNamedParameter("isActive", QueryParameterValue.bool(farmDetails.isActive()))
                    .addNamedParameter("createdAt", QueryParameterValue.timestamp(BigQueryTypes.toMicros(farmDetails.createdAt())))
                    .addNamedParameter("updatedAt", QueryParameterValue.timestamp(BigQueryTypes.toMicros(farmDetails.updatedAt())))
                    .build();
            bigQueryExecutor.execute(config);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<FarmDetails> getActiveFarms(String userID) {
        List<FarmDetails> farmDetails = new ArrayList<>();
        String sql = sqlQueryLoader.load("get_active_farm_details.sql");
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(sql)
                .setUseLegacySql(false)
                .addNamedParameter("userId", QueryParameterValue.string(userID))
                .addNamedParameter("status", QueryParameterValue.bool(true))
                .build();
        return bigQueryExecutor.query(config, this::mapRow);
    }

    private FarmDetails mapRow(FieldValueList row) {
        return new FarmDetails(
                row.get("farm_id").getStringValue(),
                row.get("user_id").getStringValue(),
                row.get("farm_name").getStringValue(),
                row.get("crop_type").getStringValue(),
                row.get("latitude").getDoubleValue(),
                row.get("longitude").getDoubleValue(),
                row.get("is_active").getBooleanValue(),
                toInstant(row.get("created_at").getTimestampValue()),
                toInstant(row.get("updated_at").getTimestampValue())
        );
    }
}
