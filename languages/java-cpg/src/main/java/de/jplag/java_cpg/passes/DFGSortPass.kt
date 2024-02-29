package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.*
import de.fraunhofer.aisec.cpg.graph.declarations.*
import de.fraunhofer.aisec.cpg.graph.edge.Properties
import de.fraunhofer.aisec.cpg.graph.scopes.BlockScope
import de.fraunhofer.aisec.cpg.graph.scopes.LoopScope
import de.fraunhofer.aisec.cpg.graph.scopes.TryScope
import de.fraunhofer.aisec.cpg.graph.statements.*
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*
import de.fraunhofer.aisec.cpg.graph.types.IncompleteType
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker
import de.fraunhofer.aisec.cpg.passes.TranslationUnitPass
import de.fraunhofer.aisec.cpg.processing.strategy.Strategy
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge
import de.jplag.java_cpg.transformation.matching.edges.Edges.BLOCK__STATEMENTS
import de.jplag.java_cpg.transformation.operations.DummyNeighbor
import de.jplag.java_cpg.transformation.operations.RemoveOperation
import de.jplag.java_cpg.transformation.operations.TransformationHelper
import java.util.*
import kotlin.reflect.KFunction1

class DFGSortPass(ctx: TranslationContext) : TranslationUnitPass(ctx) {

    private var state: State = State()
    private var stateSafe: MutableMap<Node, State?> = HashMap()
    private val walker: SubgraphWalker.IterativeGraphWalker = SubgraphWalker.IterativeGraphWalker()
    private val assignments: MutableMap<Pair<Expression, ValueDeclaration>, Assignment> = HashMap()

    override fun accept(tu: TranslationUnitDeclaration) {

        walker.strategy = sortBw(Strategy::EOG_BACKWARD)
        walker.registerOnNodeVisit2 { node: Node, parent: Node? -> handle(node) }
        val functions = tu.functions.filter { it.body != null && it.name.localName.isNotEmpty()}
        functions.forEach {calculateVariableDependencies(it) }
    }

    private fun calculateVariableDependencies(root: FunctionDeclaration) {
        val returnStatements = SubgraphWalker.flattenAST(root.body)
            .filter {it is ReturnStatement || it is UnaryOperator && it.operatorCode == "throw"}
        state = State()
        stateSafe = HashMap()

        /*
         * handle() sets DFG edges between assignments and references of the same variable
         * These DFG edges are then used in analyzeDfg()
         */
        worklist(
            returnStatements,
            { val change = handle(it);  change },
            { it.prevEOG.sortedBy { it.location?.region }.reversed() },
            succFilter = {node, change -> change}
        )

        val parentInfo = mutableMapOf<Node, ParentInfo>()
        val movableStatements = getMovableStatements(root, null, parentInfo)

        val finalState = stateSafe[root]!!

        /*
         * Sets DFG edges between statements
         */
        analyzeDfg(finalState, movableStatements, parentInfo)

        val essentialNodesOut = mutableListOf<Statement>()
        extractEssentialNodes(root.body as Statement, essentialNodesOut)
        essentialNodesOut.sortBy { it.location?.region }

        val relevantStatements: MutableList<Node> = extractRelevantStatements(essentialNodesOut, parentInfo)
        relevantStatements.removeIf { it !in movableStatements }

        removeIrrelevantStatements(relevantStatements, movableStatements, parentInfo)

        // At this point, the parentInfo map breaks. I do not know why.
        // Restore map:
        parentInfo.entries.toList().forEach { parentInfo[it.key] = it.value }


        /*
         *  Sets DFG edges between statements that contain dfg-related statements in inner blocks
         *  e.g. DeclarationStatement --> WhileStatement using the declared variable
         */
        extractTransitiveDependencies(relevantStatements, parentInfo)

        // loop dependencies are only needed to determine relevant statements, but disturb the reordering
        relevantStatements.forEach {
            it.prevDFGEdges.removeIf{edge -> relevantStatements.indexOf(edge.start) == -1 || edge.getProperty(Properties.NAME).toString().contains("loop")}
            it.nextDFGEdges.removeIf{edge -> relevantStatements.indexOf(edge.end) == -1 || edge.getProperty(Properties.NAME).toString().contains("loop") }
        }

        reorderStatements(essentialNodesOut, relevantStatements, parentInfo, root.body as Block)

    }

    private fun extractTransitiveDependencies(
        relevantStatements: MutableList<Node>,
        parentInfo: MutableMap<Node, ParentInfo>,
    ) {
        val depth = { node: Node -> parentInfo[node]?.depth ?: 0}
        val parent = { node: Node -> parentInfo[node]!!.parent}

        val dfgEdges = relevantStatements.filterIsInstance<Statement>()
            .flatMap { statement -> statement.prevDFGEdges }
            .filter { edge -> edge.start in relevantStatements}
            .filter { edge -> edge.end in relevantStatements }

        dfgEdges.forEach { edge ->
            val dfgPredecessor = edge.start
            val statement = edge.end

            var properties : MutableMap<Properties, Any?>
            val (predBlock, stmtBlock, name) = getSiblingAncestors(dfgPredecessor, statement, depth, parent)

            // no self-dependencies
            if (predBlock == stmtBlock) return@forEach

            if (stmtBlock is ReturnStatement || locationBefore(predBlock, stmtBlock)) {
                if (stmtBlock in predBlock.nextDFG) return@forEach
                // write-read dependency
                properties = mutableMapOf(Pair(Properties.NAME, name))
                predBlock.addNextDFG(stmtBlock, properties)
            } else {
                if (stmtBlock in predBlock.nextDFG) {
                    edge.addProperty(Properties.NAME, "loop$name")
                } else {
                    // this edge shows that the value reaches the next iteration
                    properties = mutableMapOf(Pair(Properties.NAME, "loop$name"))
                    predBlock.addNextDFG(stmtBlock, properties)
                }
                // read-write dependency
                properties = mutableMapOf(Pair(Properties.NAME, name))
                stmtBlock.addNextDFG(predBlock, properties)
            }
        }
    }

    private fun analyzeDfg(
        variableData: MutableMap<Declaration, VariableData>,
        movableStatements: List<Statement>,
        parentInfo: MutableMap<Node, ParentInfo>,
    ) {

        val depth = { node: Node -> parentInfo[node]?.depth ?: 0}


        val subtreeNodes = movableStatements.associateWith { SubgraphWalker.flattenAST(it) }

        val assignments = variableData.values
            .flatMap { it.assignments }

        val referenceParentStatement = assignments
            .associateWith { ref ->
                // find parent statement with the greatest depth -> immediate parent statement
                val parentStatements = movableStatements.filter { subtreeNodes[it]!!.contains(ref) }
                val immediateParent = parentStatements.maxByOrNull { depth(it) }
                immediateParent
            }

        for (statement: Statement in movableStatements) {

            val references = getImmediateChildReferences(statement, subtreeNodes)

            // map to assignments/declarations/... where current value might be set
            val valueDefinitions = references.flatMap { it.prevDFG }
                //only references to values, not methods
                .filterNot { it is MethodDeclaration }
                .distinct()
                //only local variables, not class members etc.
                .filter { assignment -> isLocal(assignment, statement) }

            // map to movable statements that contain these value definitions
            val definitionStatements = valueDefinitions.mapNotNull { assignment ->
                if (assignment is ParameterDeclaration) assignment
                else referenceParentStatement[assignment]
                    // but no self-dependencies
                    .let { if (it == statement) null else it }
            }

            definitionStatements
                //TODO: must they be excluded?
                // .filter { parent(it) == parent(statement) }
                .forEach { dfgPredecessor -> dfgPredecessor.addNextDFG(statement)
            }
        }

    }

    private fun getImmediateChildReferences(
        statement: Statement,
        subtreeNodes: Map<Statement, List<Node>>
    ): List<Reference> {
        val statements: List<Node?> = when (statement) {
            is WhileStatement -> listOf(statement.condition)
            is IfStatement -> listOf(statement.condition)
            is ForStatement -> listOf(
                statement.initializerStatement,
                statement.condition,
                statement.iterationStatement
            )

            is TryStatement -> statement.resources
            else -> listOf(statement)
        }

        return statements.filterNotNull().flatMap { subtreeNodes[it]!! }.filterIsInstance<Reference>()
    }

    private fun getSiblingAncestors(
        dfgPredecessor: Node,
        statement: Node,
        depth: (Node) -> Int,
        parent: (Node) -> Node,
    ): Triple<Node, Node, String> {
        var predBlock: Node = dfgPredecessor
        var stmtBlock: Node  = statement
        var name = "Dependency"
        if (!sameBlock(predBlock, stmtBlock)) {
            //find common ancestor, create transitive dependency
            name = "transitive$name($dfgPredecessor,$statement)"
            while (true) {
                val diff = depth(predBlock) - depth(stmtBlock)

                if (diff >= 1) {
                    predBlock = parent(predBlock)
                } else if (diff <= -1) {
                    stmtBlock = parent(stmtBlock)
                } else if (parent(predBlock) != parent(stmtBlock)) {
                    predBlock = parent(predBlock)
                    stmtBlock = parent(stmtBlock)
                } else {
                    break
                }

            }
        }
        return Triple(predBlock, stmtBlock, name)
    }

    private fun locationBefore(a: Node, b: Node) = a.location!!.region < b.location!!.region

    private fun sameBlock(a: Node, b: Node) = a.scope == b.scope

    class ParentInfo(val parent: Node, val depth: Int) {
    }

    private fun removeIrrelevantStatements(
        relevantStatements: Collection<Node>,
        statements: List<Statement>,
        parentInfo: MutableMap<Node, ParentInfo>
    ) {
        val irrelevantStatements = statements.filter { it !in relevantStatements }
            .filter { parentInfo[it]!!.parent is Block }
            .distinct()

        irrelevantStatements.forEach {
            val block = parentInfo[it]!!.parent as Block
            val index = block.statements.indexOf(it)
            val cpgNthEdge: CpgNthEdge<Block, Statement> = CpgNthEdge(BLOCK__STATEMENTS, index)
            RemoveOperation.apply(it, block, cpgNthEdge, true)

            DummyNeighbor.getInstance().clear()
        }

    }

    private fun getBlocks(statement: Statement): List<Block> {
        val statements = when (statement) {
            is Block -> listOf(statement)
            is WhileStatement -> listOf(statement.statement!!)
            is ForStatement -> listOf(statement.statement!!)
            is ForEachStatement -> listOf(statement.statement!!)
            is IfStatement ->
                if (statement.elseStatement != null) listOf(
                    statement.thenStatement!!,
                    statement.elseStatement!!
                ) else listOf(statement.thenStatement!!)

            else -> listOf(statement)
        }
        return statements.filterIsInstance<Block>()
    }

    private fun reorderStatements(
        essentialNodesOut: MutableList<Statement>,
        relevantStatements: MutableList<Node>,
        parentMap: MutableMap<Node, ParentInfo>,
        parent: Block
    ) {
        // save entry point to keep EOG graph intact at the end
        val entry = TransformationHelper.getEogBorders(parent.statements[0]).entries[0]
        val eogPred = TransformationHelper.getEntryEdges(parent.statements[0], entry, false)
            .map { it.start }

        val exit = TransformationHelper.getEogBorders(parent.statements.last()).exits[0]
        val eogSucc = TransformationHelper.getExitEdges(parent.statements.last(), listOf(exit), false)
            .map { it.end }

        val worklist = mutableListOf<Statement>()
        val done = mutableListOf<Statement>()

        val relevantStatementsInThisBlock = relevantStatements.filter { parentMap[it].let { it != null && it.parent == parent }}
        val allSuccessorsDone : (Node) -> Boolean = { it : Node -> it.nextDFG.none{ e->
                e !in done &&
                relevantStatementsInThisBlock.indexOf(e) >= 0
        }}
        relevantStatementsInThisBlock
            .filter(allSuccessorsDone)
            .map { it as Statement }
            .sortedBy {
                when (it) {
                    is ReturnStatement -> 2
                    !in essentialNodesOut -> 1
                    else -> 0
                }
            }
            .let { them -> worklist.addAll(them) }

        while (worklist.isNotEmpty()) {
            val element = worklist.removeLast()
            if (!allSuccessorsDone(element)) {
                continue
            }
            // insert before last element
            if (done.isNotEmpty()) {
                // Implicit return statement has no location, but should remain the last element
                val newSuccessor = done[0]
                if (!TransformationHelper.isAstSuccessor(element, newSuccessor)) {
                    TransformationHelper.insertBefore(element, newSuccessor)
                }
                // TODO: Make it so that this is not necessary
                DummyNeighbor.getInstance().clear()
            }
            done.add(0, element)
            if (isBlockStatement(element)) {
                val innerBlocks = getBlocks(element)
                innerBlocks.forEach {
                    reorderStatements(essentialNodesOut, relevantStatements, parentMap, it)
                }
            }

            val newElements =
                element.prevDFG
                    .asSequence()
                    .distinct()
                    .filter { it !in done && relevantStatementsInThisBlock.indexOf(it) >= 0 && it !in worklist }
                    .filter(allSuccessorsDone)
                    .map { it as Statement }
                    .toList()
            worklist.addAll(newElements)

        }
        assert(done.size == relevantStatementsInThisBlock.size && done.containsAll(relevantStatementsInThisBlock))

        parent.statementEdges.clear()
        done.forEach { parent.addStatement(it) }

        val newEntry = TransformationHelper.getEogBorders(parent.statements[0]).entries[0]

        newEntry.prevEOGEdges.filter { it.start is DummyNeighbor }.forEach {
            it.start.nextEOGEdges.remove(it)
            it.end.prevEOGEdges.remove(it)
        }
        eogPred.filter { !TransformationHelper.isEogSuccessor(it, newEntry) }
            .forEach {
                val edge = it.nextEOGEdges.find { it.end is DummyNeighbor }!!
                DummyNeighbor.getInstance().prevEOGEdges.remove(edge)
                edge.end = newEntry
                newEntry.addPrevEOG(edge)
            }


        val newExit = TransformationHelper.getEogBorders((parent.statements.last())).exits[0]
        if (exit != newExit) {
            newExit.nextEOGEdges.filter { it.end is DummyNeighbor }.forEach {
                it.start.nextEOGEdges.remove(it)
                it.end.prevEOGEdges.remove(it)
            }
            eogSucc.filter { succ -> !TransformationHelper.isEogSuccessor(exit, succ) }
                .forEach {
                    val edge = it.prevEOGEdges.find { it.start is DummyNeighbor }!!
                    edge.start = newExit
                    newExit.addNextEOG(edge)
                }
        }
    }

    private fun extractRelevantStatements(
        essentialNodesOut: MutableList<Statement>,
        parentInfo: MutableMap<Node, ParentInfo>
    ): MutableList<Node> {
        val relevantNodes: MutableSet<Node> = mutableSetOf()

        // block statements may have non-essential incoming dfg edges
        worklist(
            essentialNodesOut.filter { !isBlockStatement(it) },
            { relevantNodes.add(it) },
            { it.prevDFG.filterIsInstance<Statement>() },
            succFilter = {it, _ -> it !in relevantNodes && it !is Block}
        )

        // block statements containing relevant nodes are relevant by extension
        worklist(
            relevantNodes.map { parentInfo[it]?.parent }.distinct().filterNotNull(),
            { relevantNodes.add(it) },
            { parentInfo[it]?.parent.let { parent -> if (parent == null) listOf() else listOf(parent) } },
            succFilter = { node: Node, _: Boolean -> node is Statement}
        )

        return relevantNodes.sortedBy { it.location?.region }.toMutableList()
    }

    private fun <T, R> worklist(
        initList: List<T>, f: (T) -> R, successors: (T) -> Collection<T>?,
        succFilter: (T, R) -> Boolean = { _: T, _: R -> true },

    ) {
        val worklist = mutableListOf<T>()
        worklist.addAll(initList)
        while (worklist.isNotEmpty()) {
            val el = worklist.removeAt(0)
            val result = f(el)
            successors(el)?.filter { it !in worklist }
                ?.filter { succFilter(it, result) }
                ?.let { them -> worklist.addAll(0, them) }
        }
    }


    private fun isBlockStatement(node: Node): Boolean {
        return when (node) {
            is Block, is WhileStatement, is ForStatement, is ForEachStatement, is IfStatement -> {
                true
            }

            else -> false
        }

    }

    private fun isLocal(node: Node, reference: Node): Boolean {
        return node.location != null && node.location!!.artifactLocation == reference.location?.artifactLocation

    }

    private fun extractEssentialNodes(
        node: Statement,
        essentialNodes: MutableList<Statement>,
    ): Boolean {
        /*
            Mark the following nodes as essential:
            - method calls
            - ...nodes with side effects
            - and blocks that contain essential nodes

            not necessarily essential (includes, but not limited to):
            - control statements
            - variable declarations
        */

         when (node) {
            // Block, ForEachStatement
            is StatementHolder -> {
                val essentialChildren = node.statements
                    .filter { extractEssentialNodes(it, essentialNodes) }
                val isEssential = essentialChildren.isNotEmpty()
                if (isEssential) {
                    essentialNodes.add(node)
                    for (i in essentialChildren.indices) {
                        for (j in i+1 until essentialChildren.size) {
                            val properties: MutableMap<Properties, Any?> = mutableMapOf(Pair(Properties.NAME, "essentialsDependency"))
                            essentialChildren[i].addNextDFG(essentialChildren[j], properties)
                        }
                    }
                }
                return isEssential
            }

            is WhileStatement -> {
                val children = listOfNotNull(node.condition, node.statement)
                val isEssential = children.map { extractEssentialNodes(it, essentialNodes) }.any { it }
                if (isEssential) {
                    essentialNodes.add(node)
                    node.condition?.let { essentialNodes.add(it) }
                }
                return isEssential
            }

            is ReturnStatement, is CallExpression -> {
                essentialNodes.add(node)
                return true
            }

            is BinaryOperator -> {
                val children = listOfNotNull(node.lhs, node.rhs)
                val isEssential = children.map { extractEssentialNodes(it, essentialNodes) }.any { it }
                if (isEssential) {
                    essentialNodes.add(node)
                }
                return isEssential
            }

            is UnaryOperator -> {
                if (node.operatorCode == "throw") {
                    essentialNodes.add(node)
                    return true
                }
                return extractEssentialNodes(node.input, essentialNodes)
            }

            is AssignmentHolder -> {
                val containsFieldAssignment = node.assignments.any { assignment ->
                    assignment.target is Reference && (assignment.target as Reference).refersTo is FieldDeclaration
                }
                if (containsFieldAssignment) {
                    essentialNodes.add(node)
                    return true
                }

                val containsEssentialExpression = node.assignments.any { assignment ->
                    extractEssentialNodes(assignment.value, essentialNodes)
                }
                if (containsEssentialExpression) {
                    essentialNodes.add(node)
                    return true
                }
                return false
            }

            is Reference -> {
                // is this reference used to write a non-local value? Maybe there is a better way to do it
                val isEssential = with(node.refersTo?.scope) {
                    this !is BlockScope
                            && this !is LoopScope
                            && this !is TryScope
                } && node.access in listOf(AccessValues.WRITE, AccessValues.READWRITE)
                if (isEssential) essentialNodes.add(node)
                return isEssential
            }

            is IfStatement -> {
                val children = listOfNotNull(node.condition, node.thenStatement, node.elseStatement)
                val isEssential = children.map { extractEssentialNodes(it, essentialNodes) }.any { it }
                if (isEssential) {
                    essentialNodes.add(node)
                    node.condition?.let { essentialNodes.add(it) }
                }
                return isEssential
            }

             is ForStatement -> {
                 val children = listOfNotNull(node.initializerStatement, node.condition, node.iterationStatement, node.statement)
                 val isEssential = children.map { extractEssentialNodes(it, essentialNodes) }.any { it }
                 if (isEssential) {
                     listOfNotNull(node, node.initializerStatement, node.condition, node.iterationStatement)
                         .forEach{ essentialNodes.add(it) }
                 }
                 return isEssential
             }

             is TryStatement -> {
                 val children = mutableListOf<Statement>()
                 children.addAll(node.resources)
                 node.tryBlock?.let { children.add(it) }
                 children.addAll(node.catchClauses)
                 node.finallyBlock?.let { children.add(it) }
                 val isEssential = children.map { extractEssentialNodes(it, essentialNodes) }.any { it }
                 if (isEssential) {
                     essentialNodes.add(node)
                     essentialNodes.addAll(children)
                 }
                 return isEssential
             }

             is CatchClause -> {
                 return node.body != null && extractEssentialNodes(node.body!!, essentialNodes)
             }


        }
        return false
    }

    private fun getMovableStatements(
        statement: Node?,
        parent: Node?,
        parentInfo: MutableMap<Node, ParentInfo>,
        depth: Int = 0
    ): List<Statement> {
        if (statement == null) return mutableListOf()

        val statements: List<Statement> = when (statement) {
            is FunctionDeclaration -> {
                val children = statement.parameters.toMutableList<Node>()
                children.add(statement.body!!)
                children.flatMap { getMovableStatements(it, statement, parentInfo, depth + 1) }
            }

            is Block -> statement.statements.flatMap { getMovableStatements(it, statement, parentInfo, depth + 1) }

            is WhileStatement -> {
                val result: MutableList<Statement> = listOfNotNull(statement, statement.statement!!, statement.condition as Statement).toMutableList()
                result.addAll(getMovableStatements(statement.statement, statement, parentInfo,depth + 1))
                result
            }

            is DoStatement -> {
                val result: MutableList<Statement> = listOfNotNull(statement, statement.statement, statement.condition as Statement).toMutableList()
                result.addAll(getMovableStatements(statement.statement, statement, parentInfo,depth + 1))
                result
            }

            is ForStatement -> {
                val result: MutableList<Statement> = listOfNotNull(
                    statement,
                    statement.initializerStatement,
                    statement.condition,
                    statement.iterationStatement
                ).toMutableList()
                result.addAll(getMovableStatements(statement.statement, statement, parentInfo, depth + 1))
                result
            }

            is ForEachStatement -> {
                val result: MutableList<Statement> = listOfNotNull(
                    statement,
                    statement.variable,
                    statement.iterable
                ).toMutableList()
                result.addAll(getMovableStatements(statement.statement, statement, parentInfo, depth + 1))
                result
            }
            is IfStatement -> {
                val result = mutableListOf(statement, statement.condition as Statement)
                result.addAll(getMovableStatements(statement.thenStatement, statement, parentInfo, depth + 1))
                result.addAll(getMovableStatements(statement.elseStatement, statement, parentInfo, depth + 1))
                result
            }

            is TryStatement -> {
                val result = mutableListOf<Statement>(statement)
                val children = mutableListOf<Statement>()
                listOfNotNull(statement.tryBlock, statement.finallyBlock).forEach { children.add(it) }
                children.addAll(statement.catchClauses)
                children.flatMap { getMovableStatements(it, statement, parentInfo, depth + 1)}.forEach { result.add(it) }
                result
            }
            is LambdaExpression -> {
                getMovableStatements(statement.function, statement, parentInfo, depth + 1)
            }

            is CallExpression -> {
                val children = mutableListOf<Node>()
                statement.callee.let { if (it != null) children.add(it) }
                children.addAll(statement.parameters)
                val result = children.flatMap { getMovableStatements(it, statement, parentInfo, depth + 1) }.toMutableList()
                result.add(statement)
                result
            }

            is Statement -> {
                listOf(statement)
            }


            // only here to create entry in parentInfo
            else -> listOf()

        }
        if (parent != null) {
            parentInfo[statement] = ParentInfo(parent, depth )
        }
        statements.filter {!parentInfo.containsKey(it)}.forEach { parentInfo[it] = ParentInfo(statement, depth + 1) }

        return statements
    }

    private fun sortBw(f: KFunction1<Node, Iterator<Node>>): (Node) -> Iterator<Node> {
        return { node: Node ->
            f.call(node).asSequence().sortedBy { n1 -> n1.location?.region }.toList().asReversed().iterator()
        }
    }

    private fun handle(node: Node): Boolean {
        var change = false

        state = State()
        // base state is the combination of all nextEOG states
        node.nextEOG.mapNotNull { stateSafe[it] }
            .forEach { state.putAll(it) }

        when (node) {
            is Reference -> {
                change = state.registerReference(node)
            }

            // NewExpression is AssignmentHolder, but not usable for this analysis
            is NewExpression -> {}


            is AssignmentHolder -> {
                if (node is VariableDeclaration && node.initializer == null) {
                    // include a fake assignment with the value null
                    change = state.registerAssignment(Assignment(Literal<IncompleteType>().let { it.value = null; it }, node, node), node)
                } else {
                    change =
                        node.assignments.map { assignment: Assignment -> state.registerAssignment(assignment, node) }
                            .fold(false, Boolean::or)
                }
            }

            is UnaryOperator -> {
                handleUnaryOperator(node)
            }
        }

        if (!change && (node in stateSafe && stateSafe[node] == state)) {
            // no change
            return false
        }

        stateSafe[node] = state.copy()
        return true
    }

    private fun handleUnaryOperator(node: UnaryOperator) {
        if (!arrayOf("++", "--").contains(node.operatorCode)) return
        val reference = node.input
        if (reference !is Reference) return
        val declaration = reference.refersTo
        if (declaration !is ValueDeclaration) return

        val pair = Pair(node, declaration)
        val assignment = assignments.computeIfAbsent(pair) {
            val expr = AssignExpression()
            expr.lhs = listOf(reference)
            expr.rhs = listOf(node)
            Assignment(it.first, it.second, expr)
        }
        state.registerAssignment(assignment, node)
    }


    override fun cleanup() {
    }

    private class State : HashMap<Declaration, VariableData> {
        constructor()

        constructor(copyMap: Map<Declaration, VariableData>) : super(copyMap)


        override fun get(key: Declaration): VariableData {
            return super.computeIfAbsent(key) { VariableData() }
        }

        override fun putAll(from: Map<out Declaration, VariableData>) {
            from.keys.forEach { key: Declaration? ->
                val info: VariableData? = this[key]!!
                info?.merge(from[key])
            }
        }

        fun copy(): State {
            val copyMap: Map<Declaration, VariableData> = this.toMap()
            return State(copyMap)
        }

        fun registerAssignment(assignment: Assignment, node: de.fraunhofer.aisec.cpg.graph.Node): Boolean {
            val target = assignment.target.let { return@let if (it is Reference) it.refersTo else it }
            return this[target]!!.resolve(node)
        }

        fun registerReference(reference: Reference): Boolean {
            // may be class or enum reference
            if (reference.refersTo !is ValueDeclaration || reference.refersTo is FunctionDeclaration) return false
            return this[reference.refersTo]!!.register(reference)
        }

    }

    private class VariableData {
        private val currentReferences: MutableSet<Reference> = mutableSetOf()
        private var currentAssignments: MutableList<Node> = mutableListOf()
        private var currentDeclaration: ValueDeclaration? = null
        val references: MutableList<Reference> = mutableListOf()
        val assignments: MutableList<Node> = mutableListOf()

        fun copy(): VariableData {
            val copy = VariableData()
            copy.currentDeclaration = currentDeclaration
            copy.currentReferences.addAll(currentReferences)
            copy.currentAssignments.addAll(currentAssignments)
            copy.assignments.addAll(assignments)
            copy.references.addAll(references)
            return copy
        }

        fun resolve(node: Node): Boolean {
            val change = currentReferences
                .filter { reference: Reference -> node !in reference.prevDFG}
                .map { reference -> reference.addPrevDFG(node)
                    true
                }.any()

            assignments.add(node)
            currentAssignments.clear()

            // if it IS a declaration, then from this point on the variable is undefined yet
            if (node !is ValueDeclaration) currentAssignments.add(node)

            currentReferences.clear()
            return change
        }

        fun register(reference: Reference): Boolean {
            currentReferences.add(reference)
            if (reference !in references) {
                references.add(reference)
            }

            return currentAssignments.map {
                // reference must resolve before the next assignment
                if (reference in it.prevDFG) {
                    return false
                }
                it.addPrevDFG(reference) // read-write dependency
                return true
            }.fold(false, Boolean::or)
        }

        fun merge(other: VariableData?) {
            if (Objects.isNull(currentDeclaration)) {
                currentDeclaration = other!!.currentDeclaration
            }
            currentReferences.addAll(other!!.currentReferences.filter { it !in currentReferences })
            currentAssignments.addAll(other.currentAssignments.filter { it !in currentAssignments })
            references.addAll(other.references.filter { it !in references })
            assignments.addAll(other.assignments.filter { it !in assignments })
        }

        fun load(get: VariableData?) {
            this.currentReferences.clear()
            get?.currentReferences?.let { this.currentReferences.addAll(it) }
        }


        override fun equals(other: Any?): Boolean {
            return other is VariableData &&
                    currentReferences == other.currentReferences &&
                    currentAssignments == other.currentAssignments &&
                    references == other.references
        }

    }
}
