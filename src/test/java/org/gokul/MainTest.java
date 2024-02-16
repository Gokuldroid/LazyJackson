package org.gokul;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.gokul.lazyjackson.LazyJson;
import org.gokul.lazyjackson.LazyModule;
import org.gokul.model.DataObject;

import java.io.IOException;
import java.io.InputStream;

class MainTest {
    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.registerModule(new LazyModule(objectMapper));

        LazyJson lazyJson = new LazyJson(objectMapper);
        String content = readLazyFile();
        DataObject dataObject = lazyJson.readValue(content, DataObject.class);
        System.out.println(objectMapper.writeValueAsString(dataObject));
    }

    public static String readLazyFile() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("lazy-json-1.json");
        return IOUtils.toString(is, "UTF-8");
    }
}