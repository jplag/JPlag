package de.jplag.emf.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.eclipse.emf.ecore.EPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the language specific option for metamodel registering.
 */
class MetamodelPathOptionTest {

    // test resources:
    private static final Path METAMODEL_DIRECTORY = Path.of("src", "test", "resources", "de", "jplag");
    private static final Path BOOKSTORE_METAMODEL = METAMODEL_DIRECTORY.resolve("bookStore.ecore");
    private static final Path PLAGIARISM_METAMODEL = METAMODEL_DIRECTORY.resolve("jplag.ecore");

    // URIs from the respective metamodel files:
    private static final String BOOKSTORE_NS_URI = "http:///com.ibm.dynamic.example.bookstore.ecore";
    private static final String PLAGIARISM_NS_URI = "http://www.jplag/1.0";

    // test constants:
    private static final String JPLAG_NOT_REGISTERED_MESSAGE = "JPlag EPackage should be registered";
    private static final String BOOKSTORE_NOT_REGISTERED_MESSAGE = "Bookstore EPackage should be registered!";
    private static final String PATH_SEPARATOR = ",";

    private MetamodelPathOption option;

    @BeforeEach
    void setUp() {
        option = new MetamodelPathOption();
        // Clean up registry before each test to ensure isolation:
        EPackage.Registry.INSTANCE.remove(BOOKSTORE_NS_URI);
        EPackage.Registry.INSTANCE.remove(PLAGIARISM_NS_URI);
    }

    @Test
    void testInitialState() {
        assertFalse(option.hasValue());
        assertNull(option.getValue());
        assertFalse(option.getDescription().isBlank());
        assertFalse(option.getName().isBlank());
    }

    @Test
    void testSetNullValue() {
        option.setValue(null);
        assertFalse(option.hasValue());
    }

    @Test
    void testSingleMetamodelIsRegistered() {
        option.setValue(BOOKSTORE_METAMODEL.toString());
        assertTrue(option.hasValue());
        assertNotNull(EPackage.Registry.INSTANCE.get(BOOKSTORE_NS_URI), BOOKSTORE_NOT_REGISTERED_MESSAGE);
    }

    @Test
    void testMultipleMetamodelsAreRegistered() {
        String paths = BOOKSTORE_METAMODEL + PATH_SEPARATOR + PLAGIARISM_METAMODEL;
        option.setValue(paths);
        assertNotNull(EPackage.Registry.INSTANCE.get(BOOKSTORE_NS_URI), BOOKSTORE_NOT_REGISTERED_MESSAGE);
        assertNotNull(EPackage.Registry.INSTANCE.get(PLAGIARISM_NS_URI), JPLAG_NOT_REGISTERED_MESSAGE);
    }

    @Test
    void testWrongExtensionIsNotRegistered() {
        option.setValue(BOOKSTORE_METAMODEL.resolveSibling("bookStore.xmi").toString());
        assertNull(EPackage.Registry.INSTANCE.get(BOOKSTORE_NS_URI));
    }
}