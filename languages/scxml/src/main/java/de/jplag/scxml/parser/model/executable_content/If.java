package de.jplag.scxml.parser.model.executable_content;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.jplag.scxml.parser.util.NodeUtil;

/**
 * Represents an {@literal <if>} SCXML element, which is an executable content element used for conditional execution.
 * The {@literal <if>} element can contain {@literal <elseif>} and {@literal <else>} branches for handling multiple
 * conditions.
 * @param cond the cond attribute of the {@literal <if>} element which is the condition expression for the contained
 * executable contents to be executed
 * @param contents the list of executable contents to be executed when the condition is met
 * @param elseIfs represents the list of {@literal <elseif>} branches in the {@literal <if>} element
 * @param else_ the {@literal <else>} branch corresponding to the {@literal <if>} element, or {@code null} if not
 * present
 */
public record If(String cond, List<ExecutableContent> contents, List<ElseIf> elseIfs, Else else_) implements ExecutableContent {

    private static final Set<String> ALLOWED_CONTENTS = Set.of("raise", "if", "foreach", "log", "assign", "script", "send", "cancel");

    private static final String IF_ELEMENT = "if";
    private static final String ELSEIF_ELEMENT = "elseif";
    private static final String COND_ATTRIBUTE = "cond";

    /**
     * Constructs an If instance with the specified condition and a list of executable contents. The {@code elseIf}
     * attribute is set to an empty list and the {@code else} is set to null.
     * @param cond the cond attribute of the {@literal <if>} element which is the condition expression for the contained
     * executable contents to be executed
     * @param contents the list of executable contents to be executed when the condition is met
     */
    public If(String cond, ExecutableContent... contents) {
        this(cond, new ArrayList<>(List.of(contents)), new ArrayList<>(), null);
    }

    private static void addBranch(String branch, List<ExecutableContent> contents, List<ElseIf> elseIfs, List<Else> elses) {
        if (ELSEIF_ELEMENT.equals(branch)) {
            elseIfs.add(new ElseIf(contents));
        } else if (ELSE_ELEMENT.equals(branch)) {
            elses.add(new Else(contents));
        }
    }

    /**
     * Constructs an If statechart element from a given node with optional ElseIf or Else branches. The W3C SCXML
     * specification defines a valid {@literal <if>} element as follows:
     *
     * <pre>
     * {@code
     * <if cond="cond1">
     *   <!-- selected when "cond1" is true -->
     * <elseif cond="cond2"/>
     *   <!-- selected when "cond1" is false and "cond2" is true -->
     * <elseif cond="cond3"/>
     *   <!-- selected when "cond1" and "cond2" are false and "cond3" is true -->
     * <else/>
     *   <!-- selected when "cond1", "cond2", and "cond3" are false -->
     * </if>
     * }
     * </pre>
     * <p>
     * This syntax requires more complicated parsing as the branches and executable contents within each branch are defined
     * on the same level.
     * @param node the node to create the If object from. Must contain at least one {@literal <if>} element and optionally
     * {@literal <elseif>} or {@literal <else>} tags.
     * @return an instance of If created from the node
     * @throws IllegalArgumentException when more than one {@literal <else>} statement is present
     */
    public static If fromNode(Node node) throws IllegalArgumentException {
        NodeList childNodes = node.getChildNodes();
        List<ElseIf> elseIfs = new ArrayList<>();
        List<Else> elses = new ArrayList<>();

        String curBranch = IF_ELEMENT;
        List<ExecutableContent> curContents = new ArrayList<>();
        List<ExecutableContent> ifContents = new ArrayList<>();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            String nodeName = childNode.getNodeName();
            if (ELSEIF_ELEMENT.equals(nodeName) || ELSE_ELEMENT.equals(nodeName)) {
                if (IF_ELEMENT.equals(curBranch)) {
                    ifContents = new ArrayList<>(curContents);
                }

                addBranch(curBranch, curContents, elseIfs, elses);
                curBranch = nodeName;
                curContents.clear();
            } else if (ALLOWED_CONTENTS.contains(nodeName)) {
                curContents.add(ExecutableContent.fromNode(childNode));
            }
        }

        if (IF_ELEMENT.equals(curBranch)) {
            ifContents = curContents;
        } else {
            // Close the last branch, if there is any
            addBranch(curBranch, curContents, elseIfs, elses);
        }

        if (elses.size() > 1) {
            throw new IllegalArgumentException("<if> element may only contain at most one else branch");
        }
        return new If(NodeUtil.getAttribute(node, COND_ATTRIBUTE), ifContents, elseIfs, elses.isEmpty() ? null : elses.get(0));
    }

    @Override
    public String toString() {
        return "If {";
    }
}
