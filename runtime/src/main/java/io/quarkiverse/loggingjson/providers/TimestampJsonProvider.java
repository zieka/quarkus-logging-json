package io.quarkiverse.loggingjson.providers;

import io.quarkiverse.loggingjson.Enabled;
import io.quarkiverse.loggingjson.JsonGenerator;
import io.quarkiverse.loggingjson.JsonProvider;
import io.quarkiverse.loggingjson.JsonWritingUtils;
import io.quarkiverse.loggingjson.config.Config;
import org.jboss.logmanager.ExtLogRecord;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimestampJsonProvider implements JsonProvider, Enabled {

    private final String fieldName;
    private final DateTimeFormatter dateTimeFormatter;
    private final Config.TimestampField config;

    public TimestampJsonProvider(Config.TimestampField config) {
        this(config, "timestamp");
    }

    public TimestampJsonProvider(Config.TimestampField config, String defaultName) {
        this.config = config;
        fieldName = config.fieldName.orElse(defaultName);

        ZoneId zoneId;
        if (config.zoneId == null || "default".equals(config.zoneId)) {
            zoneId = ZoneId.systemDefault();
        } else {
            zoneId = ZoneId.of(config.zoneId);
        }

        if (!"default".equals(config.dateFormat)) {
            dateTimeFormatter = DateTimeFormatter.ofPattern(config.dateFormat).withZone(zoneId);
        } else {
            dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId);
        }

    }

    @Override
    public void writeTo(JsonGenerator generator, ExtLogRecord event) throws IOException {
        long millis = event.getMillis();
        JsonWritingUtils.writeStringField(generator, fieldName, dateTimeFormatter.format(Instant.ofEpochMilli(millis)));
    }

    @Override
    public boolean isEnabled() {
        return config.enabled.orElse(true);
    }
}
