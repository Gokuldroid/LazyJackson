package org.gokul.lazyjackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import org.gokul.lazyjackson.Lazy;

import java.io.IOException;


public class LazyDeserializer
        extends StdDeserializer<Lazy<?>>
        implements ContextualDeserializer {
    private static final long serialVersionUID = 2L; // 2.9

    /**
     * Full type of property (or root value) for which this deserializer
     * has been constructed and contextualized.
     */
    protected final JavaType _fullType;
    protected final JsonDeserializer<Object> _valueDeserializer;
    private final ObjectMapper objectMapper;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    @SuppressWarnings("unchecked")
    public LazyDeserializer(JavaType fullType, JsonDeserializer<?> deser, ObjectMapper objectMapper) {
        super(fullType);
        _fullType = fullType;
        _valueDeserializer = (JsonDeserializer<Object>) deser;
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        JsonDeserializer<?> deser = _valueDeserializer;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(_fullType.getReferencedType(), property);
        } else { // otherwise directly assigned, probably not contextual yet:
            deser = ctxt.handleSecondaryContextualization(deser, property, _fullType.getReferencedType());
        }

        if (deser == _valueDeserializer) {
            return this;
        }
        return withResolved(deser);
    }

    /*
    /**********************************************************
    /* Partial NullValueProvider impl
    /**********************************************************
     */

    /**
     * Null value varies dynamically (unlike with scalar types),
     * so let's indicate this.
     */
    @Override
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.DYNAMIC;
    }

    @Override
    public JavaType getValueType() {
        return _fullType;
    }

    @Override // since 2.12
    public LogicalType logicalType() {
        if (_valueDeserializer != null) {
            return _valueDeserializer.logicalType();
        }
        return super.logicalType();
    }

    @Override // since 2.9
    public Boolean supportsUpdate(DeserializationConfig config) {
        return (_valueDeserializer == null) ? null
                : _valueDeserializer.supportsUpdate(config);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    public Lazy<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        if (p.getCurrentToken() != JsonToken.START_OBJECT
                && p.getCurrentToken() != JsonToken.START_ARRAY) {
            System.out.println("Not efficient! lazy is used for primitive params: " + p.getParsingContext().getCurrentName());
        }
        return referenceValue(ctxt.bufferAsCopyOfValue(p));
    }

    @Override
    public Lazy<?> deserialize(JsonParser p, DeserializationContext ctxt, Lazy<?> reference) throws IOException {
        if (p.getCurrentToken() != JsonToken.START_OBJECT
                && p.getCurrentToken() != JsonToken.START_ARRAY) {
            System.out.println("Not efficient! lazy is used for primitive params: " + p.getParsingContext().getCurrentName());
        }
        TokenBuffer tb = ctxt.bufferAsCopyOfValue(p);
        return reference == null ? referenceValue(tb) : reference.update(tb);
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
                                      TypeDeserializer typeDeserializer) throws IOException {
        return deserialize(p, ctxt);
    }

    public LazyDeserializer withResolved(JsonDeserializer<?> valueDeser) {
        return new LazyDeserializer(_fullType, valueDeser, objectMapper);
    }

    @Override
    public Lazy<?> getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return Lazy.ofNullable(_valueDeserializer.getNullValue(ctxt));
    }

    public Lazy<?> referenceValue(TokenBuffer contents) {
        return new Lazy<>(contents, _fullType.getReferencedType(), objectMapper);
    }
}
