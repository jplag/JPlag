package de.jplag.endtoend.helper;

import java.io.IOException;

import de.jplag.Language;
import de.jplag.LanguageLoader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserialized a language from a json file.
 */
public class LanguageDeserializer extends JsonDeserializer<Language> {
    @Override
    public Language deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String name = jsonParser.getText();
        return LanguageLoader.getLanguage(name).orElseThrow(() -> new IllegalStateException(String.format("Language %s not found.", name)));
    }
}
