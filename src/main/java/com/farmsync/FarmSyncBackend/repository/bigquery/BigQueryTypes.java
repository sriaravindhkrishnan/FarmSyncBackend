package com.farmsync.FarmSyncBackend.repository.bigquery;

import java.time.Instant;

public final class BigQueryTypes {

    private BigQueryTypes() {
    }

    /** BigQuery TIMESTAMP (epoch micros) -> Instant */
    public static Instant toInstant(long micros) {
        long seconds = Math.floorDiv(micros, 1_000_000L);
        long remainingMicros = Math.floorMod(micros, 1_000_000L);
        return Instant.ofEpochSecond(seconds, remainingMicros * 1_000L);
    }

    /** Instant -> epoch micros for QueryParameterValue.timestamp(...) */
    public static long toMicros(Instant instant) {
        return Math.addExact(
                Math.multiplyExact(instant.getEpochSecond(), 1_000_000L),
                instant.getNano() / 1_000L
        );
    }
}