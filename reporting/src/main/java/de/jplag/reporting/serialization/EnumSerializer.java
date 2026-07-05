package de.jplag.reporting.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Serializes enums only exporting the name of the enum constant.
 */
public class EnumSerializer extends StdSerializer<Enum> {
    protected EnumSerializer() {
        super(Enum.class);
    }

    @Override
    public void serialize(Enum anEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(anEnum.name());
    }
}
