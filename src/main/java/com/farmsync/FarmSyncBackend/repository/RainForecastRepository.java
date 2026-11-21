package com.farmsync.FarmSyncBackend.repository;

import com.farmsync.FarmSyncBackend.model.RainForecastSlot;
import com.farmsync.FarmSyncBackend.repository.bigquery.BigQueryExecutor;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.QueryParameterValue;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import static com.farmsync.FarmSyncBackend.repository.bigquery.BigQueryTypes.toInstant;
import static com.farmsync.FarmSyncBackend.repository.bigquery.BigQueryTypes.toMicros;

@Repository
public class RainForecastRepository {

    private final BigQueryExecutor executor;
    private final SqlQueryLoader sqlQueryLoader;

    public RainForecastRepository(BigQueryExecutor executor, SqlQueryLoader sqlQueryLoader) {
        this.executor = executor;
        this.sqlQueryLoader = sqlQueryLoader;
    }

    // INSERT
    public void save(RainForecastSlot slot) {
        String sql = sqlQueryLoader.load("insert_rain_forecast_slot.sql");
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(sql)
                .setUseLegacySql(false)
                .addNamedParameter("slotId", QueryParameterValue.string(slot.slotId()))
                .addNamedParameter("farmId", QueryParameterValue.string(slot.farmId()))
                .addNamedParameter("forecastDate", QueryParameterValue.date(slot.forecastDate().toString()))
                .addNamedParameter("slotTime", QueryParameterValue.timestamp(toMicros(slot.slotTime())))
                .addNamedParameter("rainProbability", QueryParameterValue.float64(slot.rainProbability()))
                .addNamedParameter("status", QueryParameterValue.string(slot.status()))
                .addNamedParameter("createdAt", QueryParameterValue.timestamp(toMicros(slot.createdAt())))
                .addNamedParameter("updatedAt", QueryParameterValue.timestamp(toMicros(slot.updatedAt())))
                .build();
        executor.execute(config);
    }

    // SELECT BY FARM ID
    public List<RainForecastSlot> findByFarmId(String farmId) {
        String sql = sqlQueryLoader.load("get_slots_by_farm.sql");
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(sql)
                .setUseLegacySql(false)
                .addNamedParameter("farmId", QueryParameterValue.string(farmId))
                .build();
        return executor.query(config, this::mapRow);
    }

    // SELECT PENDING SLOTS
    public List<RainForecastSlot> findPendingSlots(String farmId) {
        String sql = sqlQueryLoader.load("get_pending_slots.sql");
        QueryJobConfiguration config = QueryJobConfiguration.newBuilder(sql)
                .setUseLegacySql(false)
                .addNamedParameter("farmId", QueryParameterValue.string(farmId))
                .addNamedParameter("status", QueryParameterValue.string("PENDING"))
                .build();
        return executor.query(config, this::mapRow);
    }

    private RainForecastSlot mapRow(FieldValueList row) {
        return new RainForecastSlot(
                row.get("slot_id").getStringValue(),
                row.get("farm_id").getStringValue(),
                LocalDate.parse(row.get("forecast_date").getStringValue()),
                toInstant(row.get("slot_time").getTimestampValue()),
                row.get("rain_probability").getDoubleValue(),
                row.get("status").getStringValue(),
                toInstant(row.get("created_at").getTimestampValue()),
                toInstant(row.get("updated_at").getTimestampValue())
        );
    }
}