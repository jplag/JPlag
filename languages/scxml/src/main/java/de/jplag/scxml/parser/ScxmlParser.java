package de.jplag.scxml.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.jplag.ParsingException;
import de.jplag.scxml.parser.model.State;
import de.jplag.scxml.parser.model.Statechart;
import de.jplag.scxml.parser.model.Transition;
import de.jplag.scxml.parser.model.executable_content.Action;
import de.jplag.scxml.parser.model.executable_content.ExecutableContent;
import de.jplag.scxml.parser.util.NodeUtil;

/**
 * An SCXML parser implementation based on a Simple API for XML (SAX) parser. Constructs a Statechart object during the
 * parse.
 */
public class ScxmlParser {

    private static final String STATE_ELEMENT = "state";
    private static final String PARALLEL_STATE_ELEMENT = "parallel";
    private static final String INITIAL_ELEMENT = "initial";
    private static final String ONENTRY_ELEMENT = "onentry";
    private static final String ONEXIT_ELEMENT = "onexit";
    private static final String TRANSITION_ELEMENT = "transition";

    private static final String NAME_ATTRIBUTE = "name";
    private static final String ID_ATTRIBUTE = "id";
    private static final String INITIAL_ATTRIBUTE = "initial";
    private static final String TARGET_ATTRIBUTE = "target";
    private static final String EVENT_ATTRIBUTE = "event";
    private static final String CONDITION_ATTRIBUTE = "cond";

    private final DocumentBuilder builder;
    private final List<String> initialStateTargets = new ArrayList<>();

    /**
     * Constructs a new ScxmlParser used to parse SCXML documents.
     * @throws ParserConfigurationException when the document builder for parsing the XML files cannot be constructed
     */
    public ScxmlParser() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

    /**
     * Parses the given SCXML file using Javax and constructs a Statechart object. Two passes through the document are
     * performed: In the first pass, all {@literal <initial>} elements within states are iterated over to resolve initial
     * states. In the second pass, the whole document is visited. This is necessary because an initial state may occur in
     * the document prior to the transitions pointing to it.
     * @param file the SCXML file to parse
     * @return the statechart constructed from the input statechart file
     * @throws ParsingException when the statechart could not be parsed
     */
    public Statechart parse(File file) throws ParsingException {
        try {
            Document document = builder.parse(file);
            Element element = document.getDocumentElement();
            resolveInitialStates(element);
            return visitRoot(element);
        } catch (SAXException | IOException | IllegalArgumentException e) {
            throw new ParsingException(file, "failed to parse statechart: " + e.getMessage());
        }
    }

    private void resolveInitialStates(Node root) {
        List<Node> initialElements = NodeUtil.getNodesRecursive(root, INITIAL_ELEMENT);
        List<Transition> transitions = initialElements.stream().map(this::visitInitialTransition).toList();
        initialStateTargets.addAll(transitions.stream().map(Transition::target).toList());
    }

    private <T> List<T> visitChildElements(Node root, Set<String> childNames, Function<Node, T> visitorFunction) {
        return new ArrayList<>(NodeUtil.getChildNodes(root, childNames).stream().map(visitorFunction).toList());
    }

    private Statechart visitRoot(Node node) {
        String name = NodeUtil.getAttribute(node, NAME_ATTRIBUTE);
        assert name != null : "statechart element must have name attribute";

        List<State> states = visitChildElements(node, Set.of(STATE_ELEMENT, PARALLEL_STATE_ELEMENT), this::visitState);
        return new Statechart(name, states);
    }

    private State visitState(Node node) {
        String id = NodeUtil.getAttribute(node, ID_ATTRIBUTE);
        assert id != null : "state element must have id attribute";

        boolean initial = initialStateTargets.contains(id) || NodeUtil.getAttribute(node, INITIAL_ATTRIBUTE) != null;
        boolean parallel = PARALLEL_STATE_ELEMENT.equals(node.getNodeName());

        Node child = NodeUtil.getFirstChild(node, INITIAL_ELEMENT);
        assert !(parallel && child != null) : "parallel state " + id + " must not have initial element";

        List<Action> actions = visitChildElements(node, Set.of(ONENTRY_ELEMENT, ONEXIT_ELEMENT), this::visitAction);
        List<Transition> transitions = visitChildElements(node, Set.of(TRANSITION_ELEMENT), this::visitTransition);
        List<State> states = visitChildElements(node, Set.of(STATE_ELEMENT, PARALLEL_STATE_ELEMENT), this::visitState);
        return new State(id, transitions, states, actions, initial, parallel);
    }

    private Action visitAction(Node node) throws IllegalArgumentException {
        if (node == null) {
            return null;
        }
        Action.Type type = ONENTRY_ELEMENT.equals(node.getNodeName()) ? Action.Type.ON_ENTRY : Action.Type.ON_EXIT;
        return new Action(type, visitExecutableContents(node));
    }

    private List<ExecutableContent> visitExecutableContents(Node node) throws IllegalArgumentException {
        return visitChildElements(node, ExecutableContent.ALLOWED_XML_ELEMENTS, ExecutableContent::fromNode);
    }

    private Transition visitInitialTransition(Node node) {
        List<Node> transitionNodes = NodeUtil.getChildNodes(node, TRANSITION_ELEMENT);
        assert !transitionNodes.isEmpty() : "initial element must contain transition child";
        Transition transition = visitTransition(transitionNodes.get(0));
        assert transition.isInitial() : "transition is not an initial transition";
        return transition;
    }

    private Transition visitTransition(Node node) throws IllegalArgumentException {
        return new Transition(NodeUtil.getAttribute(node, TARGET_ATTRIBUTE), NodeUtil.getAttribute(node, EVENT_ATTRIBUTE),
                NodeUtil.getAttribute(node, CONDITION_ATTRIBUTE), visitExecutableContents(node),
                // Set timed attribute to false initially, may be updated later in the State class
                false);
    }
}
