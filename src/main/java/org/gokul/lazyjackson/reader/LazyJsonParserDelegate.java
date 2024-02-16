package org.gokul.lazyjackson.reader;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.core.util.JsonParserDelegate;
import org.gokul.lazyjackson.io.SingleCopyCharArrayReader;

import java.io.IOException;
import java.io.StringWriter;

public class LazyJsonParserDelegate extends JsonParserDelegate {
    private final SingleCopyCharArrayReader singleCopyCharArrayReader;

    public LazyJsonParserDelegate(JsonParser d, SingleCopyCharArrayReader singleCopyCharArrayReader) {
        super(d);
        this.singleCopyCharArrayReader = singleCopyCharArrayReader;
    }

    public String getCurrentStructure() throws IOException {
        if (getCurrentToken() != JsonToken.START_OBJECT
                && getCurrentToken() != JsonToken.START_ARRAY) {
            System.out.println("Not efficient! lazy is used for primitive param: " + getParsingContext().getCurrentName());;
        }

        StringWriter out = new StringWriter();
        try (JsonGenerator gen = new JsonFactory().createGenerator(out)) {
           gen.copyCurrentStructure(this);
        }
        return out.toString();
    }

    public SingleCopyCharArrayReader getLazyReader() {
        return this.singleCopyCharArrayReader;
    }
}
