package org.gokul.lazyjackson.deserializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import org.gokul.lazyjackson.Lazy;
import org.gokul.lazyjackson.utils.Preconditions;


public class LazyDeserializers extends Deserializers.Base
        implements java.io.Serializable {
    private final ObjectMapper objectMapper;

    public LazyDeserializers(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override // since 2.7
    public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType,
                                                         DeserializationConfig config, BeanDescription beanDesc,
                                                         TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) {
        if (refType.hasRawClass(Lazy.class)) {
            Preconditions.checkArgument(contentTypeDeserializer == null, "Lazy jackson doesn't support contentTypeDeserializer");
            return new LazyDeserializer(refType, contentDeserializer, objectMapper);
        }
        return null;
    }
}
