package org.gokul.lazyjackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CacheProvider;
import com.fasterxml.jackson.databind.cfg.DefaultCacheProvider;
import org.gokul.lazyjackson.io.SingleCopyCharArrayReader;
import org.gokul.lazyjackson.reader.LazyJsonParserDelegate;

import java.io.IOException;

public class LazyJson {
    private final ObjectMapper objectMapper;

    public LazyJson(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T readValue(String content, Class<T> valueType)
            throws IOException {
        return objectMapper.readValue(createParser(content, objectMapper), valueType);
    }

    public static JsonParser createParser(String content, ObjectMapper objectMapper) throws IOException {
        char[] contentArray = content.toCharArray();
        SingleCopyCharArrayReader singleCopyCharArrayReader = new SingleCopyCharArrayReader(contentArray);
        return new LazyJsonParserDelegate(
                objectMapper.createParser(singleCopyCharArrayReader),
                singleCopyCharArrayReader);
    }
}
