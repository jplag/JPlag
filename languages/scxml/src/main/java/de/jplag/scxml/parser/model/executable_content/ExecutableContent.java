package de.jplag.scxml.parser.model.executable_content;

import static de.jplag.scxml.parser.model.executable_content.SimpleExecutableContent.Type.*;

import java.util.Set;

import org.w3c.dom.Node;

import de.jplag.scxml.parser.model.StatechartElement;
import de.jplag.scxml.parser.util.NodeUtil;

/**
 * Represents executable content in an SCXML statechart, which are elements that can be executed during state
 * transitions, state entry, state exit or in conditional statements.
 */
public interface ExecutableContent extends StatechartElement {

    /**
     * String constant for the "event" attribute.
     */
    String EVENT_ATTRIBUTE = "event";

    /**
     * String constant for the "sendid" attribute.
     */
    String SEND_ID_ATTRIBUTE = "sendid";

    /**
     * String constant for the "delay" attribute.
     */
    String DELAY_ATTRIBUTE = "delay";

    /**
     * String constant for the {@literal <raise>} element.
     */
    String RAISE_ELEMENT = "raise";

    /**
     * String constant for the {@literal <if>} element.
     */
    String IF_ELEMENT = "if";

    /**
     * String constant for the {@literal <foreach>} element.
     */
    String FOREACH_ELEMENT = "foreach";

    /**
     * String constant for the {@literal <log>} element.
     */
    String LOG_ELEMENT = "log";

    /**
     * String constant for the {@literal <assign>} element.
     */
    String ASSIGN_ELEMENT = "assign";

    /**
     * String constant for the {@literal <script>} element.
     */
    String SCRIPT_ELEMENT = "script";

    /**
     * String constant for the {@literal <send>} element.
     */
    String SEND_ELEMENT = "send";

    /**
     * String constant for the {@literal <cancel>} element.
     */
    String CANCEL_ELEMENT = "cancel";

    /**
     * String constant for the {@literal <else>} element.
     */
    String ELSE_ELEMENT = "else";

    /**
     * String constant for the {@literal <elseif>} element.
     */
    String ELSE_IF_ELEMENT = "elseif";

    /**
     * Defines the set of allowed XML element names that are considered valid executable content. {@literal <else>} and
     * {@literal <elseif>} elements are not allowed as they may only present as children of an {@literal <if>} element.
     */
    Set<String> ALLOWED_XML_ELEMENTS = Set.of(RAISE_ELEMENT, IF_ELEMENT, FOREACH_ELEMENT, LOG_ELEMENT, ASSIGN_ELEMENT, SCRIPT_ELEMENT, SEND_ELEMENT,
            CANCEL_ELEMENT);

    /**
     * Constructs a concrete instance of ExecutableContent based on the name of the given node.
     * @param node the node to create the ExecutableContent from
     * @return the constructed ExecutableContent
     * @throws IllegalArgumentException if the node name is not allowed or the executable content could not be created
     */
    static ExecutableContent fromNode(Node node) throws IllegalArgumentException {
        return switch (node.getNodeName()) {
            case IF_ELEMENT -> If.fromNode(node);
            case RAISE_ELEMENT -> new SimpleExecutableContent(RAISE);
            case ASSIGN_ELEMENT -> new SimpleExecutableContent(ASSIGNMENT);
            case SCRIPT_ELEMENT -> new SimpleExecutableContent(SCRIPT);
            case FOREACH_ELEMENT -> new SimpleExecutableContent(FOREACH);
            case LOG_ELEMENT -> new SimpleExecutableContent(LOG);
            case SEND_ELEMENT -> new Send(NodeUtil.getAttribute(node, EVENT_ATTRIBUTE), NodeUtil.getAttribute(node, DELAY_ATTRIBUTE));
            case CANCEL_ELEMENT -> new Cancel(NodeUtil.getAttribute(node, SEND_ID_ATTRIBUTE));
            default -> throw new IllegalArgumentException(
                    "ExecutableContent.fromNode: invalid node " + node.getNodeName() + node.getParentNode().getNodeName());
        };
    }
}
