package de.jplag.reporting.jsonfactory.serializer;

import java.io.File;
import java.io.IOException;
import java.io.Serial;

import de.jplag.reporting.FilePathUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class FileSerializer extends StdSerializer<File> {

    @Serial
    private static final long serialVersionUID = 5944655736767387268L; // generated

    /**
     * Constructor used by the fasterxml.jackson
     */
    public FileSerializer() {
        this(null);
    }

    public FileSerializer(Class<File> fileClass) {
        super(fileClass);
    }

    @Override
    public void serialize(File file, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(FilePathUtil.forceRelativePath(file.toPath()).toString());
    }
}