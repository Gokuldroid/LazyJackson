package org.gokul.lazyjackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class LazyJson {
    private final ObjectMapper objectMapper;

    public LazyJson(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T readValue(String content, Class<T> valueType)
            throws IOException {
        return objectMapper.readValue(objectMapper.createParser(content), valueType);
    }
}
