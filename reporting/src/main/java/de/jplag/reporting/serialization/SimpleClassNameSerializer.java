package de.jplag.reporting.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Serializes any object with just it's simple classname converted to uppercase.
 */
public class SimpleClassNameSerializer extends StdSerializer<Object> {
    protected SimpleClassNameSerializer() {
        super(Object.class);
    }

    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(o.getClass().getSimpleName().toUpperCase());
    }
}
