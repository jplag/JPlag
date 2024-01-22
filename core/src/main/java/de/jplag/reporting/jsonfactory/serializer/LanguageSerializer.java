package de.jplag.reporting.jsonfactory.serializer;

import java.io.IOException;

import de.jplag.Language;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LanguageSerializer extends StdSerializer<Language> {

    /**
     * Constructor used by the fasterxml.jackson
     */
    public LanguageSerializer() {
        this(null);
    }

    public LanguageSerializer(Class<Language> languageClass) {
        super(languageClass);
    }

    @Override
    public void serialize(Language language, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(language.getName());
    }
}
