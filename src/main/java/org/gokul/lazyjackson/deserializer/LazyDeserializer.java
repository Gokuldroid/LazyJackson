package org.gokul.lazyjackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import org.gokul.lazyjackson.Lazy;
import org.gokul.lazyjackson.reader.LazyJsonParserDelegate;

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
    public AccessPattern getEmptyAccessPattern() {
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

    /**
     * By default we assume that updateability mostly relies on value
     * deserializer; if it supports updates, typically that's what
     * matters. So let's just delegate.
     */
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
        LazyJsonParserDelegate lazyParser = (LazyJsonParserDelegate) p;
        String structure = lazyParser.getCurrentStructure();
        return referenceValue(structure);
    }

    @Override
    public Lazy<?> deserialize(JsonParser p, DeserializationContext ctxt, Lazy<?> reference) throws IOException {
        return referenceValue(((LazyJsonParserDelegate) p).getCurrentStructure());
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
                                      TypeDeserializer typeDeserializer) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) { // can this actually happen?
            return getNullValue(ctxt);
        }
        return deserialize(p, ctxt);
    }

    public LazyDeserializer withResolved(JsonDeserializer<?> valueDeser) {
        return new LazyDeserializer(_fullType, valueDeser, objectMapper);
    }

    @Override
    public Lazy<?> getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        // 07-May-2019, tatu: [databind#2303], needed for nested ReferenceTypes
        Object nullValue = _valueDeserializer.getNullValue(ctxt);
        if (nullValue == null) {
            return new Lazy<>(null, null, null);
        }
        return referenceValue(nullValue.toString());
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        // 07-May-2019, tatu: I _think_ this needs to align with "null value" and
        //    not necessarily with empty value of contents? (used to just do "absent"
        //    so either way this seems to me like an improvement)
        return getNullValue(ctxt);
    }

    /**
     * As of Jackson 2.14 we will either return either same as
     * {@link #getNullValue} or {@code null}: see
     * {@like Jdk8Module#configureReadAbsentLikeNull(boolean)} for
     * details.
     */
    public Object getAbsentValue(DeserializationContext ctxt) throws JsonMappingException {
        return getNullValue(ctxt);
    }
    public Lazy<?> referenceValue(String contents) {
        return new Lazy<>(contents, _fullType.getReferencedType(), objectMapper);
    }
}
