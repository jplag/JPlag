package de.jplag.java_cpg.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.TranslationResult;
import de.fraunhofer.aisec.cpg.graph.Component;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.FunctionDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.NamespaceDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.DeclarationStatement;
import de.fraunhofer.aisec.cpg.graph.statements.DoStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ForStatement;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg.graph.statements.Statement;
import de.fraunhofer.aisec.cpg.graph.statements.WhileStatement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.AssignExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;
import de.fraunhofer.aisec.cpg.processing.IStrategy;
import de.fraunhofer.aisec.cpg.processing.strategy.Strategy;
import de.fraunhofer.aisec.cpg.sarif.Region;
import de.jplag.java_cpg.transformation.operations.TransformationUtil;

import com.google.common.collect.Iterators;

/**
 * This class defines the order of visitation of the CPG {@link Node}s.
 */
public class NodeOrderStrategy implements IStrategy<Node> {

    private static final boolean USE_CALL_GRAPH_ORDER = true;
    private List<MethodDeclaration> methodOrder;
    private final boolean detailedTraversal;

    /**
     * Creates a new {@link NodeOrderStrategy}.
     * @param detailedTraversal whether to do a more detailed traversal (e.g. visiting the condition of loops and if
     * statements before the body)
     */
    public NodeOrderStrategy(boolean detailedTraversal) {
        this.detailedTraversal = detailedTraversal;
    }

    @Override
    public @NotNull Iterator<Node> getIterator(Node node) {
        if (node instanceof TranslationResult translationResult) {
            this.methodOrder = new MethodOrderStrategy(detailedTraversal).setupMethodCallGraphOrder(translationResult);
            return Strategy.INSTANCE.AST_FORWARD(node);
        } else if (node instanceof Component c) {
            return walkComponent(c);
        } else if (node instanceof TranslationUnitDeclaration tu) {
            return walkTranslationUnit(tu);
        } else if (node instanceof RecordDeclaration recordDecl) {
            return walkRecord(recordDecl);
        } else if (node instanceof MethodDeclaration methodDecl) {
            return walkMethod(methodDecl);
        } else if (node instanceof WhileStatement whileStatement) {
            return walkWhileStatement(whileStatement);
        } else if (node instanceof DoStatement doStatement) {
            return walkDoWhileStatement(doStatement);
        } else if (node instanceof IfStatement ifStatement) {
            return walkIfStatement(ifStatement);
        } else if (node instanceof ForStatement forStatement) {
            return walkForStatement(forStatement);
        } else if (node instanceof DeclarationStatement declarationStatement) {
            return walkDeclarationStatement(declarationStatement);
        } else if (node instanceof AssignExpression assignExpression) {
            return walkAssignExpression(assignExpression);
        } else if (node instanceof Block block) {
            return walkBlock(block);
        } else {
            return Strategy.INSTANCE.AST_FORWARD(node);
        }
    }

    private Iterator<Node> walkAssignExpression(AssignExpression assignExpression) {
        return assignExpression.getRhs().stream().map(n -> (Node) n).iterator();
    }

    private Iterator<Node> walkDeclarationStatement(DeclarationStatement declarationStatement) {
        return declarationStatement.getDeclarations().stream().map(n -> (Node) n).iterator();
    }

    private Iterator<Node> walkTranslationUnit(TranslationUnitDeclaration tu) {
        List<RecordDeclaration> topLevelRecords = getTopLevelRecords(tu);
        return List.<Node>copyOf(topLevelRecords).iterator();
    }

    private Iterator<Node> walkComponent(Component c) {
        Map<Boolean, List<TranslationUnitDeclaration>> filePartition = c.getTranslationUnits().stream()
                .collect(Collectors.groupingBy(tu -> getTopLevelRecords(tu).stream().anyMatch(NodeOrderStrategy::isMainClass)));

        List<TranslationUnitDeclaration> mainFiles = filePartition.getOrDefault(true, List.of());
        List<TranslationUnitDeclaration> otherFiles = filePartition.getOrDefault(false, List.of());

        return Iterators.concat(mainFiles.iterator(), otherFiles.iterator());
    }

    private static boolean isMainClass(RecordDeclaration recordDeclaration) {
        return recordDeclaration.getMethods().stream().anyMatch(NodeOrderStrategy::isMainMethod);
    }

    private List<RecordDeclaration> getTopLevelRecords(Node node) {
        List<RecordDeclaration> result = new ArrayList<>();
        List<Node> declarations = new ArrayList<>(List.of(node));
        while (!declarations.isEmpty()) {
            Node declaration = declarations.removeFirst();
            switch (declaration) {
                case Component component -> declarations.addAll(component.getTranslationUnits());
                case TranslationUnitDeclaration tu -> declarations.addAll(tu.getDeclarations());
                case NamespaceDeclaration namespaceDeclaration -> declarations.addAll(namespaceDeclaration.getDeclarations());
                case RecordDeclaration recordDeclaration -> result.add(recordDeclaration);
                case null, default -> {
                    // do nothing
                }
            }

        }
        return result;
    }

    private Iterator<Node> walkRecord(RecordDeclaration recordDecl) {
        List<MethodDeclaration> functions = new ArrayList<>();
        functions.addAll(recordDecl.getConstructors());
        functions.addAll(recordDecl.getMethods());
        return Iterators.concat(recordDecl.getFields().iterator(),
                functions.stream().filter(m -> !Objects.isNull(m.getBody())).sorted(this::walkMethods).iterator(),
                recordDecl.getTemplates().iterator(), recordDecl.getRecords().iterator());
    }

    private static Iterator<Node> walkMethod(MethodDeclaration methodDecl) {
        if (!methodDecl.hasBody()) {
            return Iterators.concat(methodDecl.getParameters().iterator());
        }
        return Iterators.concat(methodDecl.getParameters().iterator(), List.of(methodDecl.getBody()).iterator());
    }

    static boolean isMainMethod(FunctionDeclaration function) {
        return function instanceof MethodDeclaration method && method.isStatic() && method.getName().getLocalName().equals("main")
                && method.getReturnTypes().size() == 1 && method.getReturnTypes().getFirst().getTypeName().equals("void");
    }

    @NotNull
    private static Iterator<Node> walkBlock(Block block) {
        return block.getStatements().stream().map(n -> (Node) n).iterator();
    }

    /**
     * Finds all child {@link Node}s of the given {@link Statement} in the order determined by the
     * {@link NodeOrderStrategy}.
     * @param statement the statement
     * @return a list of all child nodes
     */
    public static List<Node> flattenStatement(Statement statement) {
        List<Node> astChildren = SubgraphWalker.INSTANCE.flattenAST(statement);
        NodeOrderStrategy strategy = new NodeOrderStrategy(true);
        Node entry = TransformationUtil.getEntry(statement);

        List<Node> nodes = new ArrayList<>(astChildren.size());
        SubgraphWalker.IterativeGraphWalker walker = new SubgraphWalker.IterativeGraphWalker();
        walker.setStrategy(node -> Iterators.filter(strategy.getIterator(node), astChildren::contains));
        walker.registerOnNodeVisit(nodes::add);
        walker.iterate(entry);
        return nodes;
    }

    private Iterator<Node> walkDoWhileStatement(DoStatement doStatement) {
        Node condition = doStatement.getCondition();
        Node body = doStatement.getStatement();
        if (Objects.isNull(body)) {
            if (detailedTraversal) {
                return Stream.of(condition).iterator();
            } else {
                return Collections.emptyIterator();
            }

        }
        if (detailedTraversal) {
            return Stream.of(condition, body).iterator();
        } else {
            return Stream.of(body).iterator();
        }

    }

    private Iterator<Node> walkForStatement(ForStatement forStatement) {
        Node body = forStatement.getStatement();
        List<Node> nodes = new ArrayList<>();
        if (detailedTraversal) {
            nodes.add(forStatement.getInitializerStatement());
            nodes.add(forStatement.getCondition());
            nodes.add(forStatement.getIterationStatement());
        }
        nodes.add(body);
        return nodes.stream().filter(Objects::nonNull).iterator();
    }

    private Iterator<Node> walkIfStatement(IfStatement ifStatement) {
        if (detailedTraversal) {
            return Stream.<Node>of(ifStatement.getCondition(), ifStatement.getThenStatement(), ifStatement.getElseStatement())
                    .filter(Objects::nonNull).iterator();
        }
        return Stream.<Node>of(ifStatement.getThenStatement(), ifStatement.getElseStatement()).filter(Objects::nonNull).iterator();

    }

    private int walkMethods(MethodDeclaration method1, MethodDeclaration method2) {
        if (USE_CALL_GRAPH_ORDER && Objects.isNull(methodOrder)) {
            return Comparator.<MethodDeclaration, Region>comparing(m -> m.getLocation() == null ? null : m.getLocation().getRegion()).compare(method1,
                    method2);
        }

        return Comparator.comparing(methodOrder::indexOf).compare(method1, method2);

    }

    private Iterator<Node> walkWhileStatement(WhileStatement whileStatement) {
        Node condition = whileStatement.getCondition();
        Node body = whileStatement.getStatement();
        if (Objects.isNull(body)) {
            if (detailedTraversal) {
                return Stream.of(condition).iterator();
            } else {
                return Collections.emptyIterator();
            }

        }
        if (detailedTraversal) {
            return Stream.of(condition, body).iterator();
        } else {
            return Stream.of(body).iterator();
        }

    }
}
