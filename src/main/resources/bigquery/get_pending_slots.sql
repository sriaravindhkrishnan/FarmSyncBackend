SELECT
    slot_id,
    farm_id,
    forecast_date,
    slot_time,
    rain_probability,
    status,
    created_at,
    updated_at
FROM
    `project-d344ccfa-338f-43e6-80f.farmsync.rain_forecast_slots`
WHERE
    farm_id = @farmId
  AND status = @status
ORDER BY
    slot_time ASC;