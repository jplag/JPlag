package de.jplag.scxml.parser.model.executable_content;

import de.jplag.scxml.parser.util.NodeUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record If(String cond, List<ExecutableContent> contents, List<ElseIf> elseIfs,
                 Else else_) implements ExecutableContent {

    private static final Set<String> ALLOWED_CONTENTS = Set.of(
            "raise", "if", "foreach", "log", "assign", "script", "send", "cancel"
    );

    private static final String IF_ELEMENT = "if";
    private static final String ELSEIF_ELEMENT = "elseif";
    private static final String COND_ATTRIBUTE = "cond";

    public If(String cond, ExecutableContent... contents) {
        this(cond, new ArrayList<>(List.of(contents)), new ArrayList<>(), null);
    }

    private static void addBranch(String branch, List<ExecutableContent> contents, List<ElseIf> elseIfs, List<Else> elses) {
        if (branch.equals(ELSEIF_ELEMENT)) {
            elseIfs.add(new ElseIf(contents));
        } else if (branch.equals(ELSE_ELEMENT)) {
            elses.add(new Else(contents));
        }
    }

    /**
     * Constructs an If statechart element from a given node
     * with optional ElseIf or Else branches.
     * The W3C SCXML specification defines a valid {@literal <if>} element as follows:
     * <p>
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
     * This syntax requires more complicated parsing as the branches and
     * executable contents within each branch are defined on the same level.
     *
     * @param node the node to create the If object from. Must
     *             contain at least one {@literal <if>} element and optionally
     *             {@literal <elseif>} or {@literal <else>} tags.
     * @throws IllegalArgumentException when more than one {@literal <else>}
     *                                  statement is present
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
            if (nodeName.equals(ELSEIF_ELEMENT) || nodeName.equals(ELSE_ELEMENT)) {
                if (curBranch.equals(IF_ELEMENT)) {
                    ifContents = new ArrayList<>(curContents);
                }

                addBranch(curBranch, curContents, elseIfs, elses);
                curBranch = nodeName;
                curContents.clear();
            } else if (ALLOWED_CONTENTS.contains(nodeName)) {
                curContents.add(ExecutableContent.fromNode(childNode));
            }
        }

        if (curBranch.equals(IF_ELEMENT)) {
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
        return "If";
    }
}
