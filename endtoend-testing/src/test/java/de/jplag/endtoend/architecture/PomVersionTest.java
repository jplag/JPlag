package de.jplag.endtoend.architecture;

import static de.jplag.testutils.AssertionUtils.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Ensures that version tags in pom.xml files are defined properly.
 */
class PomVersionTest {
    private static final String POM_XML_NAME = "pom.xml";
    private static final String REVISION_REFERENCE = "${revision}";

    private static final String REGEX_VERSION_REFERENCE = "\\$\\{((version\\..+)|(revision))\\}";
    private static final String REGEX_PLUGIN_VERSION_REFERENCE = "\\$\\{version\\.plugin\\..+\\}";

    private static final String XPATH_JOIN = " | ";

    private static final String XPATH_PARENT_VERSION = "/pom:project/pom:parent/pom:version";
    private static final String XPATH_MODULE_VERSION = "/pom:project/pom:version";
    private static final String XPATH_DEPENDENCY_VERSION = "/pom:project/pom:dependencies/pom:dependency/pom:version";
    private static final String XPATH_DEPENDENCY_MANAGEMENT_VERSION = "/pom:project/pom:dependencyManagement/pom:dependencies/pom:dependency/pom:version";
    private static final String XPATH_PLUGIN_VERSION = "/pom:project/pom:build/pom:plugins/pom:plugin/pom:version";
    private static final String XPATH_PLUGIN_MANAGEMENT_VERSION = "/pom:project/pom:build/pom:pluginManagement/pom:plugins/pom:plugin/pom:version";

    private static final String XPATH_ALL_DEPENDENCY_VERSION = XPATH_DEPENDENCY_VERSION + XPATH_JOIN + XPATH_DEPENDENCY_MANAGEMENT_VERSION;
    private static final String XPATH_ALL_PLUGIN_VERSION = XPATH_PLUGIN_VERSION + XPATH_JOIN + XPATH_PLUGIN_MANAGEMENT_VERSION;
    private static final String XPATH_EXTERNAL_VERSION = XPATH_ALL_DEPENDENCY_VERSION + XPATH_JOIN + XPATH_ALL_PLUGIN_VERSION;
    private static final String XPATH_IMPORT_VERSION = XPATH_DEPENDENCY_VERSION + XPATH_JOIN + XPATH_PLUGIN_VERSION;

    private static final File projectRoot = new File("..");
    private static final File projectRootPom = new File(projectRoot, POM_XML_NAME);

    private static final List<String> ALLOWED_EXTERNAL_DEPENDENCIES = List.of("de.fraunhofer.aisec:cpg-core", "de.fraunhofer.aisec:cpg-language-java",
            "de.fraunhofer.aisec:cpg-analysis", "org.jetbrains.kotlin:kotlin-stdlib-jdk8", "org.jetbrains.kotlin:kotlin-test");

    private static final List<String> ALLOWED_EXTERNAL_PLUGINS = List.of("org.jetbrains.kotlin:kotlin-maven-plugin");

    static List<File> getAllPomFiles() {
        List<File> collector = new ArrayList<>();
        getAllPomFiles(projectRoot, collector);
        return collector;
    }

    static List<File> getAllChildPomFiles() {
        List<File> poms = getAllPomFiles();
        poms.remove(projectRootPom);
        return poms;
    }

    static void getAllPomFiles(File scanDir, List<File> collector) {
        for (File file : scanDir.listFiles()) {
            if (file.isFile() && file.getName().equals(POM_XML_NAME)) {
                collector.add(file);
            }
            if (file.isDirectory()) {
                getAllPomFiles(file, collector);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("getAllChildPomFiles")
    @DisplayName("Ensure all child poms use " + REVISION_REFERENCE + " to reference parent")
    void testModuleParentVersion(File pom) throws IOException, JDOMException {
        List<Element> versionTags = scanXpath(pom, XPATH_PARENT_VERSION);
        assertEquals(1, versionTags.size());
        assertEquals(REVISION_REFERENCE, versionTags.getFirst().getText(),
                "Invalid parent version in " + pom.getPath() + ". Should be: " + REVISION_REFERENCE);
    }

    @ParameterizedTest
    @MethodSource("getAllChildPomFiles")
    @DisplayName("Ensure all no child pom declares its own version")
    void testNoVersionForChildPoms(File pom) throws IOException, JDOMException {
        List<Element> versionTags = scanXpath(pom, XPATH_MODULE_VERSION);
        assertEquals(0, versionTags.size(), "Pom file " + pom.getPath() + " should not declare its version");
    }

    @ParameterizedTest
    @MethodSource("getAllChildPomFiles")
    @DisplayName("Ensures no child pom declares any versions for dependencies and plugins")
    void testNoDependencyVersionsInChildPom(File pom) throws IOException, JDOMException {
        List<Element> versionTags = scanXpath(pom, XPATH_EXTERNAL_VERSION);
        assertAll(versionTags, this::failForVersionTag);
    }

    @Test
    @DisplayName("Ensures the root project sets its version as " + REVISION_REFERENCE)
    void testRootProjectVersion() throws IOException, JDOMException {
        List<Element> versionTag = scanXpath(projectRootPom, XPATH_MODULE_VERSION);
        assertEquals(1, versionTag.size(), "No version declared in root pom file");
        assertEquals(REVISION_REFERENCE, versionTag.getFirst().getText(), "Version in root pom should be defined as " + REVISION_REFERENCE);
    }

    @Test
    @DisplayName("Ensures that the root pom does not reverence any versions in the dependencies or plugins section (dependencyManagement/pluginManagement should be used instead)")
    void testRootProjectDependencyVersions() throws IOException, JDOMException {
        List<Element> directlyReferencedVersions = scanXpath(projectRootPom, XPATH_IMPORT_VERSION);
        assertAll(directlyReferencedVersions, this::failForVersionTag);
    }

    @Test
    @DisplayName("Ensures That all version inside the root dependencyManagement are defined using 'version.*' variables")
    void testRootProjectDependencyManagementVersions() throws IOException, JDOMException {
        List<Element> managementVersions = scanXpath(projectRootPom, XPATH_DEPENDENCY_MANAGEMENT_VERSION);
        assertAll(managementVersions, (versionTag) -> {
            if (!versionTag.getText().matches(REGEX_VERSION_REFERENCE)) {
                failForVersionTag(versionTag);
            }
        });
    }

    @Test
    @DisplayName("Ensures That all version inside the root pluginManagement are defined using 'version.plugin.*' variables")
    void testRootProjectPluginManagementVersions() throws IOException, JDOMException {
        List<Element> managementVersions = scanXpath(projectRootPom, XPATH_PLUGIN_MANAGEMENT_VERSION);
        assertAll(managementVersions, (versionTag) -> {
            if (!versionTag.getText().matches(REGEX_PLUGIN_VERSION_REFERENCE)) {
                failForVersionTag(versionTag);
            }
        });
    }

    private Document parsePom(File pom) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(pom);
    }

    private List<Element> scanXpath(File pom, String xpathExpression) throws IOException, JDOMException {
        return scanXpath(parsePom(pom), xpathExpression);
    }

    private List<Element> scanXpath(Document doc, String xpathExpression) {
        XPathFactory factory = XPathFactory.instance();
        XPathBuilder<Element> builder = new XPathBuilder<>(xpathExpression, new ElementFilter());
        builder.setNamespace(Namespace.getNamespace("pom", "http://maven.apache.org/POM/4.0.0"));
        XPathExpression<Element> expression = builder.compileWith(factory);
        return expression.evaluate(doc);
    }

    private void failForVersionTag(@NotNull Element versionTag) {
        Namespace ns = versionTag.getNamespace();
        Element definition = versionTag.getParentElement();
        String groupId = definition.getChildText("groupId", ns);
        String artifactId = definition.getChildText("artifactId", ns);
        // Handle inherited groupId from parent
        if (groupId == null) {
            groupId = "de.jplag";
        }
        String key = groupId + ":" + artifactId;
        // Allow external dependencies and plugins
        if (ALLOWED_EXTERNAL_DEPENDENCIES.contains(key) || ALLOWED_EXTERNAL_PLUGINS.contains(key)) {
            return;
        }
        // Allow property-based external dependencies
        if (groupId.startsWith("${") && (ALLOWED_EXTERNAL_DEPENDENCIES.stream().anyMatch(dep -> dep.endsWith(":" + artifactId))
                || ALLOWED_EXTERNAL_PLUGINS.stream().anyMatch(plugin -> plugin.endsWith(":" + artifactId)))) {
            return;
        }
        // Allow internal JPlag dependencies with ${revision}
        if ("de.jplag".equals(groupId) && "${revision}".equals(versionTag.getText())) {
            return;
        }
        String definitionString = "{groupId: " + groupId + ", artifactId:" + artifactId + ", version:" + versionTag.getText() + "}";
        Assertions.fail("Invalid version tag found for: " + definitionString);
    }

}
