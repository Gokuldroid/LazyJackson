package org.gokul.lazyjackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gokul.lazyjackson.deserializer.LazyDeserializers;
import org.gokul.lazyjackson.seralizer.LazySerializers;

public class LazyModule extends Module {

    private final ObjectMapper objectMapper;

    public LazyModule(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getModuleName() {
        return "LazyModule";
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(Module.SetupContext context) {
        context.addDeserializers(new LazyDeserializers(objectMapper));
        context.addSerializers(new LazySerializers());
        context.addTypeModifier(new LazyTypeModifier());
    }
}
