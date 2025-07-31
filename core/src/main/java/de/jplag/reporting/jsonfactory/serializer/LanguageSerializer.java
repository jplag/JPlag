package de.jplag.reporting.jsonfactory.serializer;

import java.io.IOException;
import java.io.Serial;

import de.jplag.Language;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Custom Jackson serializer for the {@link Language} class. Serializes a Language object by writing its identifier as a
 * JSON string.
 */
public class LanguageSerializer extends StdSerializer<Language> {

    @Serial
    private static final long serialVersionUID = 5944655736767387268L; // generated

    /**
     * Constructor used by the fasterxml.jackson.
     */
    public LanguageSerializer() {
        this(null);
    }

    /**
     * Constructor that passes the class type to the superclass.
     * @param languageClass The Language class type (can be null)
     */
    public LanguageSerializer(Class<Language> languageClass) {
        super(languageClass);
    }

    @Override
    public void serialize(Language language, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(language.getIdentifier());
    }
}
