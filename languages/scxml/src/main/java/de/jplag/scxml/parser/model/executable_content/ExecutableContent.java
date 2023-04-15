package de.jplag.scxml.parser.model.executable_content;

import de.jplag.scxml.parser.model.StatechartElement;
import de.jplag.scxml.parser.util.NodeUtil;
import org.w3c.dom.Node;

import java.util.Set;

import static de.jplag.scxml.parser.model.executable_content.SimpleExecutableContent.Type.*;

public interface ExecutableContent extends StatechartElement {

    /**
     * Defines the set of allowed XML element names that are considered
     * valid executable content.
     * <else> and <elseif> elements are not allowed as they may only present
     * as children of an <if> element.
     */
    Set<String> ALLOWED_XML_ELEMENTS = Set.of(
            "raise", "if", "foreach", "log", "assign", "script", "send", "cancel"
    );

    String ELSE_ELEMENT = "else";
    String EVENT_ATTRIBUTE = "event";
    String SEND_ID_ATTRIBUTE = "sendid";
    String DELAY_ATTRIBUTE = "delay";

    /**
     * Constructs a concrete instance of ExecutableContent based on the name
     * of the given node.
     *
     * @param node the node to create the ExecutableContent from
     * @return the constructed ExecutableContent
     * @throws IllegalArgumentException if the node name is not allowed or the
     * executable content could not be created
     */
    static ExecutableContent fromNode(Node node) throws IllegalArgumentException {
        return switch (node.getNodeName()) {
            case "if" -> If.fromNode(node);
            case "raise" -> new SimpleExecutableContent(RAISE);
            case "assign" -> new SimpleExecutableContent(ASSIGNMENT);
            case "script" -> new SimpleExecutableContent(SCRIPT);
            case "foreach" -> new SimpleExecutableContent(FOREACH);
            case "log" -> new SimpleExecutableContent(LOG);
            case "send" ->
                    new Send(NodeUtil.getAttribute(node, EVENT_ATTRIBUTE), NodeUtil.getAttribute(node, DELAY_ATTRIBUTE));
            case "cancel" -> new Cancel(NodeUtil.getAttribute(node, SEND_ID_ATTRIBUTE));
            default ->
                    throw new IllegalArgumentException("ExecutableContent.fromNode: invalid node " + node.getNodeName() + node.getParentNode().getNodeName());
        };
    }
}
