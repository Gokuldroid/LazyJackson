package org.gokul.lazyjackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.Optional;

@AllArgsConstructor
public class Lazy<T> {

    private static final Lazy<?> EMPTY = new Lazy<>(null, null, null);

    T value;

    @Getter
    TokenBuffer tokens;

    @Getter
    JavaType javaType;

    ObjectMapper objectMapper;

    public Lazy() {
        throw new IllegalCallerException("Lazy can't be created with empty constructor");
    }

    public Lazy(TokenBuffer tokens, JavaType refType, ObjectMapper objectMapper) {
        this.tokens = tokens;
        this.javaType = refType;
        this.objectMapper = objectMapper;
    }

    public Lazy(T value) {
        this.value = value;
    }

    public boolean isPresent() {
        return tokens != null && !tokens.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public T get() throws IOException {
        if (value == null) {
            value = (T) objectMapper.readValue(asParser(), javaType);
        }
        return value;
    }

    public JsonParser asParser() {
        return tokens.asParser();
    }

    public JsonParser asParserOnFirstToken() throws IOException {
        return tokens.asParserOnFirstToken();
    }

    public static <T> Lazy<T> empty() {
        @SuppressWarnings("unchecked")
        Lazy<T> t = (Lazy<T>) EMPTY;
        return t;
    }

    public static <T> Lazy<T> ofNullable(T object) {
        return object == null ? empty() : new Lazy<>(object);
    }

    public Lazy<?> update(TokenBuffer tb) {
        this.tokens = tb;
        this.value = null;
        return this;
    }
}
