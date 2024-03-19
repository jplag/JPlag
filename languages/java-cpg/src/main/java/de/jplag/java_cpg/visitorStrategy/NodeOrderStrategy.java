package de.jplag.java_cpg.visitorStrategy;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.TranslationResult;
import de.fraunhofer.aisec.cpg.graph.Component;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.*;
import de.fraunhofer.aisec.cpg.graph.scopes.BlockScope;
import de.fraunhofer.aisec.cpg.graph.scopes.LoopScope;
import de.fraunhofer.aisec.cpg.graph.scopes.TryScope;
import de.fraunhofer.aisec.cpg.graph.scopes.ValueDeclarationScope;
import de.fraunhofer.aisec.cpg.graph.statements.*;
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

    private static final boolean useCallGraphOrder = true;
    private List<MethodDeclaration> methodOrder;

    /**
     * Creates a new {@link NodeOrderStrategy}.
     */
    public NodeOrderStrategy() {

    }

    @Override
    public @NotNull Iterator<Node> getIterator(Node node) {
        if (node instanceof TranslationResult translationResult) {
            this.methodOrder = new MethodOrderStrategy().setupMethodCallGraphOrder(translationResult);
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
        } else if (node instanceof Block block) {
            return walkBlock(block);
        } else if (node.getScope() instanceof BlockScope || node.getScope() instanceof LoopScope || node.getScope() instanceof TryScope
                || node.getScope() instanceof ValueDeclarationScope) {
            return Strategy.INSTANCE.EOG_FORWARD(node);
        } else {
            return Strategy.INSTANCE.AST_FORWARD(node);
        }
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

    private static boolean isMainClass(RecordDeclaration record) {
        return record.getMethods().stream().anyMatch(NodeOrderStrategy::isMainMethod);
    }

    private List<RecordDeclaration> getTopLevelRecords(Node node) {
        List<RecordDeclaration> result = new ArrayList<>();
        List<Node> declarations = new ArrayList<>(List.of(node));
        while (!declarations.isEmpty()) {
            Node declaration = declarations.removeFirst();
            if (declaration instanceof Component component) {
                declarations.addAll(component.getTranslationUnits());
            } else if (declaration instanceof TranslationUnitDeclaration tu) {
                declarations.addAll(tu.getDeclarations());
            } else if (declaration instanceof NamespaceDeclaration namespaceDeclaration) {
                declarations.addAll(namespaceDeclaration.getDeclarations());
            } else if (declaration instanceof RecordDeclaration recordDeclaration) {
                result.add(recordDeclaration);
            } else {
                // do nothing
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
        return block.getStatements().stream().map(TransformationUtil::getEntry).iterator();
    }

    /**
     * Finds all child {@link Node}s of the given {@link Statement} in the order determined by the
     * {@link NodeOrderStrategy}.
     * @param statement the statement
     * @return a list of all child nodes
     */
    public static List<Node> flattenStatement(Statement statement) {
        List<Node> astChildren = SubgraphWalker.INSTANCE.flattenAST(statement);
        NodeOrderStrategy strategy = new NodeOrderStrategy();
        Node entry = TransformationUtil.getEntry(statement);

        List<Node> nodes = new ArrayList<>(astChildren.size());
        SubgraphWalker.IterativeGraphWalker walker = new SubgraphWalker.IterativeGraphWalker();
        walker.setStrategy(node -> Iterators.filter(strategy.getIterator(node), astChildren::contains));
        walker.registerOnNodeVisit(nodes::add);
        walker.iterate(entry);
        return nodes;
    }

    private Iterator<Node> walkDoWhileStatement(DoStatement doStatement) {
        // Condition is visited already at this point
        Node body = doStatement.getStatement();
        if (Objects.isNull(body)) {
            return Collections.emptyIterator();
        }
        return Stream.of(body).iterator();

    }

    private Iterator<Node> walkForStatement(ForStatement forStatement) {
        // Condition is visited already at this point
        Node body = forStatement.getStatement();
        if (Objects.isNull(body)) {
            return Collections.emptyIterator();
        }
        return Stream.of(body).iterator();
    }

    private Iterator<Node> walkIfStatement(IfStatement ifStatement) {
        // Condition is already visited at this point
        return Stream.<Node>of(ifStatement.getThenStatement(), ifStatement.getElseStatement()).filter(Objects::nonNull).iterator();
    }

    private int walkMethods(MethodDeclaration method1, MethodDeclaration method2) {
        if (useCallGraphOrder && Objects.isNull(methodOrder)) {
            return Comparator.<MethodDeclaration, Region>comparing(m -> m.getLocation() == null ? null : m.getLocation().getRegion()).compare(method1,
                    method2);
        }

        return Comparator.comparing(methodOrder::indexOf).compare(method1, method2);

    }

    private Iterator<Node> walkWhileStatement(WhileStatement whileStatement) {
        // Condition is visited already at this point
        Node body = whileStatement.getStatement();
        if (Objects.isNull(body)) {
            return Collections.emptyIterator();
        }
        return Stream.of(body).iterator();
    }
}
