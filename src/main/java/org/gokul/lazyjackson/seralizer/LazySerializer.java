package org.gokul.lazyjackson.seralizer;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import lombok.SneakyThrows;
import org.gokul.lazyjackson.Lazy;

public class LazySerializer
        extends ReferenceTypeSerializer<Lazy<?>> // since 2.9
{
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************
    /* Constructors, factory methods
    /**********************************************************
     */

    protected LazySerializer(ReferenceType fullType, boolean staticTyping,
                             TypeSerializer vts, JsonSerializer<Object> ser) {
        super(fullType, staticTyping, vts, ser);
    }

    protected LazySerializer(LazySerializer base, BeanProperty property,
                             TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper,
                             Object suppressableValue, boolean suppressNulls) {
        super(base, property, vts, valueSer, unwrapper,
                suppressableValue, suppressNulls);
    }

    @Override
    protected ReferenceTypeSerializer<Lazy<?>> withResolved(BeanProperty prop,
                                                            TypeSerializer vts, JsonSerializer<?> valueSer,
                                                            NameTransformer unwrapper) {
        return new LazySerializer(this, prop, vts, valueSer, unwrapper,
                _suppressableValue, _suppressNulls);
    }

    @Override
    public ReferenceTypeSerializer<Lazy<?>> withContentInclusion(Object suppressableValue,
                                                                 boolean suppressNulls) {
        return new LazySerializer(this, _property, _valueTypeSerializer,
                _valueSerializer, _unwrapper,
                suppressableValue, suppressNulls);
    }

    /*
    /**********************************************************
    /* Abstract method impls
    /**********************************************************
     */

    @Override
    protected boolean _isValuePresent(Lazy<?> value) {
        return value.isPresent();
    }

    @SneakyThrows
    @Override
    protected Object _getReferenced(Lazy<?> value) {
        return value.get();
    }

    @Override
    @SneakyThrows
    protected Object _getReferencedIfPresent(Lazy<?> value) {
        return value.isPresent() ? value.get() : null;
    }
}