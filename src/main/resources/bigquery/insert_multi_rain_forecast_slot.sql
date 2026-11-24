INSERT INTO
    `project-d344ccfa-338f-43e6-80f.farmsync.rain_forecast_slots`
(
    slot_id,
    farm_id,
    forecast_date,
    slot_time,
    rain_probability,
    status,
    created_at,
    updated_at
)
SELECT
    slot.slot_id,
    slot.farm_id,
    DATE(slot.forecast_date),
    PARSE_DATETIME('%Y-%m-%dT%H:%M', slot.slot_time),
    slot.rain_probability,
    slot.status,
    slot.created_at,
    slot.updated_at
FROM
    UNNEST(@slots) AS slot;