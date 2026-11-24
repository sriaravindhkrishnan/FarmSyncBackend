package com.farmsync.FarmSyncBackend.repository;

import com.farmsync.FarmSyncBackend.model.RainForecastSlot;
import com.farmsync.FarmSyncBackend.repository.bigquery.BigQueryExecutor;
import org.springframework.stereotype.Repository;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.QueryParameterValue;
import com.google.cloud.bigquery.StandardSQLTypeName;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class RainSlotRepository {

    private final BigQueryExecutor bigQueryExecutor;
    private final SqlQueryLoader sqlQueryLoader;
    public RainSlotRepository(BigQueryExecutor bigQueryExecutor, SqlQueryLoader sqlQueryLoader) {
        this.bigQueryExecutor = bigQueryExecutor;
        this.sqlQueryLoader = sqlQueryLoader;

    }
    public boolean saveRows(List<RainForecastSlot> slots) {
        try {
            String sql = sqlQueryLoader.load("insert_multi_rain_forecast_slot.sql");
            QueryParameterValue[] structValues = slots.stream()
                    .map(s -> {
                        Map<String, QueryParameterValue> fields = new HashMap<>();
                        fields.put("slot_id", QueryParameterValue.string(s.farmId()));
                        fields.put("farm_id", QueryParameterValue.string(s.slotId()));
                        fields.put("forecast_date", QueryParameterValue.string(String.valueOf(s.forecastDate())));
                        fields.put("slot_time", QueryParameterValue.string(String.valueOf(s.slotTime())));
                        fields.put("rain_probability", QueryParameterValue.of(s.rainProbability(), StandardSQLTypeName.FLOAT64));
                        fields.put("status", QueryParameterValue.string(s.status()));
                        fields.put("created_at", QueryParameterValue.string(s.createdAt()));
                        fields.put("updated_at", QueryParameterValue.string(s.updatedAt()));
                        return QueryParameterValue.struct(fields);
                    })
                    .toArray(QueryParameterValue[]::new);

            QueryParameterValue slotsArrayParam = QueryParameterValue.array(
                    structValues,
                    StandardSQLTypeName.STRUCT
            );

            QueryJobConfiguration config = QueryJobConfiguration.newBuilder(sql)
                    .addNamedParameter("slots", slotsArrayParam)
                    .build();
            bigQueryExecutor.execute(config);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

