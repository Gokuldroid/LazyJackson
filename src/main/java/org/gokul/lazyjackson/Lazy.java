package org.gokul.lazyjackson;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class Lazy<T> {

    T value;


    String rawValue;

    JavaType javaType;

    private final ObjectMapper objectMapper;

    public Lazy() {
        throw new IllegalCallerException("Lazy can't be created with empty constructor");
    }

    public Lazy(String rawValue, JavaType refType, ObjectMapper objectMapper) {
        this.rawValue = rawValue;
        this.javaType = refType;
        this.objectMapper = objectMapper;
    }

    public boolean isPresent() {
        return rawValue != null;
    }

    @SuppressWarnings("unchecked")
    public T get() throws IOException {
        if (value == null) {
            value = (T) objectMapper.readValue(LazyJson.createParser(rawValue, objectMapper), javaType);
        }
        return value;
    }

    public Lazy<T> update(Object value) {
        this.value = (T) value;
        return this;
    }
}
