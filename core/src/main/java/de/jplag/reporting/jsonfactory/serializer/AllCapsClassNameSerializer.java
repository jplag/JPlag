package de.jplag.reporting.jsonfactory.serializer;

import java.io.IOException;
import java.io.Serial;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * This class serializes {@link Object}s for the JSON export using Jackson. It serializes objects as their class name in
 * a BIG_AND_FAT representation.
 * @param <T> The class type
 */
public class AllCapsClassNameSerializer<T> extends StdSerializer<T> {

    @Serial
    private static final long serialVersionUID = 8062712944669950300L;

    /**
     * Constructor used by the fasterxml.jackson.
     */
    public AllCapsClassNameSerializer() {
        this(null);
    }

    /**
     * Constructor needed to allow call with null, as super(null) is ambiguous.
     */
    private AllCapsClassNameSerializer(Class<T> tClass) {
        super(tClass);
    }

    @Override
    public void serialize(T t, JsonGenerator generator, SerializerProvider provider) throws IOException {
        String className = t.getClass().getSimpleName();
        className = className.replaceAll("([a-z])([A-Z])", "$1_$2");
        className = className.toUpperCase();
        generator.writeString(className);
    }
}