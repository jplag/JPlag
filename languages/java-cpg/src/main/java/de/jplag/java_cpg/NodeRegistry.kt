package de.jplag.java_cpg

import de.fraunhofer.aisec.cpg.graph.Node
import de.jplag.java_cpg.token.semantic.SemanticVector

object NodeRegistry {

    private val nodeMap: MutableMap<Node, SemanticVector> = mutableMapOf()

    fun registerNodeData(node: Node, value: SemanticVector) {
        nodeMap[node] = value
    }
    fun getNodeData(node: Node): SemanticVector? {
        return nodeMap[node]
    }

    fun clear() {
        nodeMap.clear()
    }
}