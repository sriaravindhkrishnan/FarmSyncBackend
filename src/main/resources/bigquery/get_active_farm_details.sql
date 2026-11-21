SELECT
    farm_id,
    user_id,
    farm_name,
    crop_type,
    latitude,
    longitude,
    is_active,
    created_at,
    updated_at
FROM
    `project-d344ccfa-338f-43e6-80f.farmsync.farms`
WHERE
    user_id = @userId
  AND is_active = @status
ORDER BY
    created_at ASC;