package de.jplag.cli;

import static java.nio.file.attribute.AclEntryPermission.EXECUTE;
import static java.nio.file.attribute.AclEntryPermission.READ_ACL;
import static java.nio.file.attribute.AclEntryPermission.READ_ATTRIBUTES;
import static java.nio.file.attribute.AclEntryPermission.READ_DATA;
import static java.nio.file.attribute.AclEntryPermission.READ_NAMED_ATTRS;
import static java.nio.file.attribute.AclEntryPermission.SYNCHRONIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.cli.options.CliOptions;
import de.jplag.cli.test.ExampleResult;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.FileException;

class OutputFileGeneratorTest {
    private final JPlagResult testResult;

    private OutputFileGeneratorTest() throws IOException, ExitException {
        testResult = ExampleResult.getExampleResult();
    }

    @Test
    void testWriteCsvEnabled() throws IOException {
        Path dir = Files.createTempDirectory("jplagCsvTest");
        CliOptions options = new CliOptions();
        options.advanced.csvExport = true;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(3, Objects.requireNonNull(Files.list(dir)).count());
    }

    @Test
    void testWriteCsvDisabled() throws IOException {
        Path dir = Files.createTempDirectory("jplagCsvTest");
        CliOptions options = new CliOptions();
        options.advanced.csvExport = false;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(0, Objects.requireNonNull(Files.list(dir)).count());
    }

    @Test
    void testWriteCsvNonWritable() throws IOException {
        Path dir = Files.createTempDirectory("jplagCsvTest");
        FileStore fs = Files.getFileStore(dir);
        if (fs.supportsFileAttributeView(PosixFileAttributeView.class)) {
            Files.setPosixFilePermissions(dir, Set.of(PosixFilePermission.OWNER_READ));
        } else if (fs.supportsFileAttributeView(AclFileAttributeView.class)) {
            AclFileAttributeView aclView = Files.getFileAttributeView(dir, AclFileAttributeView.class);
            AclEntry entry = AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(aclView.getOwner())
                    .setPermissions(EnumSet.of(EXECUTE, READ_DATA, READ_ATTRIBUTES, READ_ACL, READ_NAMED_ATTRS, SYNCHRONIZE)).build();
            aclView.setAcl(Collections.singletonList(entry));
        }
        CliOptions options = new CliOptions();
        options.advanced.csvExport = true;
        OutputFileGenerator.generateCsvOutput(testResult, dir, options);

        assertEquals(0, Objects.requireNonNull(Files.list(dir)).count());
    }

    @Test
    void testWriteNormalResultFile() throws IOException, FileException {
        Path resFile = Files.createTempFile("jplagTestResult", ".jplag");

        OutputFileGenerator.generateJPlagResultFile(testResult, resFile);

        assertTrue(Files.size(resFile) > 0);
    }
}
