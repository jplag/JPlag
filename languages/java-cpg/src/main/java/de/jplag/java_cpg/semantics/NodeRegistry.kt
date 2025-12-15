package de.jplag.java_cpg.semantics

import de.fraunhofer.aisec.cpg.graph.Node

object NodeRegistry {

    private val nodeMap: MutableMap<Node, MutableMap<String, Any>> = mutableMapOf()

    fun registerNodeData(node: Node, key: String, value: Any) {
        val dataMap = nodeMap.getOrPut(node) { mutableMapOf() }
        dataMap[key] = value
    }
    fun getNodeData(node: Node, key: String): Any? {
        return nodeMap[node]?.get(key)
    }

    fun clear() {
        nodeMap.clear()
    }
}