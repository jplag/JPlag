package de.jplag.cli.antlrtesttool;

import de.jplag.antlr.AbstractAntlrParserAdapter;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;

/**
 * A graphical test tool for antlr languages
 */
public class AntlrLanguageTester extends JPanel {
    private TestRunner runner;
    private JTree output;
    private Highlighter highlighter;

    /**
     * New instance
     * @param adapter The adapter to test
     * @throws RuntimeException -
     */
    public AntlrLanguageTester(AbstractAntlrParserAdapter<?> adapter) {
        try {
            this.runner = new TestRunner(adapter);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        setLayout(new GridLayout(1, 2));

        add(buildTestInput());
        add(buildOutputPanel());
    }

    private JPanel buildTestInput() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        JTextArea textInput = new JTextArea();
        textInput.setTabSize(3);
        inputPanel.add(textInput, BorderLayout.CENTER);

        highlighter = new DefaultHighlighter();
        textInput.setHighlighter(highlighter);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        inputPanel.add(buttonsPanel, BorderLayout.NORTH);

        JButton runButton = new JButton("Run");
        runButton.addActionListener((event) -> {
            runTest(textInput.getText());
        });
        buttonsPanel.add(runButton);

        return inputPanel;
    }

    private JPanel buildOutputPanel() {
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());

        output = new JTree();
        output.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Nothing here yet")));
        output.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        output.addTreeSelectionListener((event) -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) output.getLastSelectedPathComponent();
            highlighter.removeAllHighlights();
            if(node != null) {
                TreeEntry data = (TreeEntry) node.getUserObject();
                if (data.start() != -1 && data.end() != -1) {
                    try {
                        highlighter.addHighlight(data.start(), data.end(), new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN));
                    } catch (BadLocationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        outputPanel.add(new JScrollPane(output), BorderLayout.CENTER);

        return outputPanel;
    }

    private void runTest(String inputData) {
        try {
            output.setModel(new DefaultTreeModel(runner.runTest(inputData)));
            output.revalidate();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
