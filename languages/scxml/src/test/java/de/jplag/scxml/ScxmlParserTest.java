package de.jplag.scxml;

import static de.jplag.scxml.parser.model.executable_content.SimpleExecutableContent.Type.ASSIGNMENT;
import static de.jplag.scxml.parser.model.executable_content.SimpleExecutableContent.Type.SCRIPT;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import de.jplag.ParsingException;
import de.jplag.scxml.parser.ScxmlParser;
import de.jplag.scxml.parser.model.State;
import de.jplag.scxml.parser.model.Statechart;
import de.jplag.scxml.parser.model.Transition;
import de.jplag.scxml.parser.model.executable_content.Cancel;
import de.jplag.scxml.parser.model.executable_content.Else;
import de.jplag.scxml.parser.model.executable_content.ElseIf;
import de.jplag.scxml.parser.model.executable_content.ExecutableContent;
import de.jplag.scxml.parser.model.executable_content.If;
import de.jplag.scxml.parser.model.executable_content.Send;
import de.jplag.scxml.parser.model.executable_content.SimpleExecutableContent;
import de.jplag.scxml.util.StateBuilder;
import de.jplag.testutils.FileUtil;

class ScxmlParserTest {

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "statecharts");
    private final File baseDirectory = BASE_PATH.toFile();

    private static final String[] TEST_SUBJECTS = {"simple.scxml", "timed_transition.scxml", "conditional.scxml", "complex.scxml"};

    // Helper methods for less verbose construction of transitions

    private static Transition transition(String target, String event, List<ExecutableContent> contents) {
        return new Transition(target, event, null, contents, false);
    }

    private static Transition transition(String target, List<ExecutableContent> contents) {
        return transition(target, null, contents);
    }

    private static Transition transition(String target, String event, String cond) {
        return new Transition(target, event, cond, new ArrayList<>(), false);
    }

    private static Transition transition(String target, String event) {
        return transition(target, event, new ArrayList<>());
    }

    private static Transition transition(String target) {
        return transition(target, (String) null);
    }

    @Test
    void canParseSimpleStatechart() throws ParsingException, ParserConfigurationException, SAXException, IOException {
        File testFile = new File(baseDirectory, TEST_SUBJECTS[0]);
        Statechart actual = new ScxmlParser().parse(testFile);

        State start = new StateBuilder("Start").setInitial().addTransitions(transition("Blinking", "user.press_button")).build();
        State mainRegion = new StateBuilder("main_region").addSubstates(start).build();
        Statechart expected = new Statechart("Statechart", List.of(mainRegion));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void canParseTimedTransition() throws ParsingException, ParserConfigurationException, SAXException, IOException {
        File testFile = new File(baseDirectory, TEST_SUBJECTS[1]);
        Statechart actual = new ScxmlParser().parse(testFile);

        State start = new StateBuilder("Start").addTransitions(Transition.makeTimed(transition("Next", List.of(new SimpleExecutableContent(SCRIPT)))))
                .build();
        Statechart expected = new Statechart("Statechart", List.of(start));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void canParseConditional() throws ParserConfigurationException, ParsingException, IOException, SAXException {
        File testFile = new File(baseDirectory, TEST_SUBJECTS[2]);
        Statechart actual = new ScxmlParser().parse(testFile);
        ElseIf elseIf = new ElseIf(List.of(new SimpleExecutableContent(SimpleExecutableContent.Type.RAISE)));
        Else _else = new Else(List.of(new SimpleExecutableContent(SimpleExecutableContent.Type.RAISE)));
        If firstIf = new If("counter % 3 == 0", List.of(new Send("toggleB", "1s")), List.of(elseIf), _else);
        If secondIf = new If("cond", List.of(new SimpleExecutableContent(ASSIGNMENT)), new ArrayList<>(), null);

        State start = new StateBuilder("Start").addOnEntry(firstIf, secondIf).build();
        Statechart expected = new Statechart("statechart", List.of(start));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void canParseComplexStatechart() throws ParsingException, ParserConfigurationException, SAXException, IOException {
        File testFile = new File(baseDirectory, TEST_SUBJECTS[3]);
        Statechart actual = new ScxmlParser().parse(testFile);

        State start = new StateBuilder("Start").setInitial()
                .addTransitions(transition("Blinking", "user.press_button", List.of(new SimpleExecutableContent(ASSIGNMENT)))).build();

        State light = new StateBuilder("Light").addTransitions(transition("Dark")).addOnEntry(new If("true", new SimpleExecutableContent(ASSIGNMENT)))
                .build();

        State dark = new StateBuilder("Dark").addTransitions(transition("Start", null, "t == 5"), transition("Light", "C"))
                .addOnEntry(new Send("A", "1s")).addOnExit(new Cancel("B")).build();

        State blinking = new StateBuilder("Blinking").addSubstates(light, dark).addTransitions(transition("Start", "user.press_button"))
                .addOnEntry(new SimpleExecutableContent(ASSIGNMENT)).build();

        State mainRegion = new StateBuilder("main_region").addSubstates(start, blinking).build();
        Statechart expected = new Statechart("Statechart", List.of(mainRegion));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @AfterEach
    void tearDown() {
        FileUtil.clearFiles(new File(BASE_PATH.toString()), ScxmlLanguage.VIEW_FILE_EXTENSION);
    }
}
