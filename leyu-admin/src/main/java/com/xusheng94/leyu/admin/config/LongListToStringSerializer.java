package com.xusheng94.leyu.admin.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

public class LongListToStringSerializer extends JsonSerializer<List<Long>> {
    @Override
    public void serialize(List<Long> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (Long l : value) {
            gen.writeString(l.toString());
        }
        gen.writeEndArray();
    }
}
