package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.Node
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker
import de.fraunhofer.aisec.cpg.passes.TranslationUnitPass
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * A pass that prints all found nodes to the console.
 */
class PrintPass(ctx: TranslationContext) : TranslationUnitPass(ctx) {

    companion object {
        @JvmStatic
        val KClass: KClass<PrintPass> = PrintPass::class
    }

    override fun cleanup() {}

    override fun accept(translationUnitDeclaration: TranslationUnitDeclaration) {
        val graphWalker = SubgraphWalker.IterativeGraphWalker()
        graphWalker.registerOnNodeVisit { node: Node ->
            var code = node.code
            if (Objects.isNull(code)) {
                code = "~no code~"
            } else if (code!!.length > 20) {
                code = code.substring(0, 20) + "..."
            }
            System.out.printf("%s[%s]%n", node.javaClass, code)
        }
        for (node in translationUnitDeclaration.astChildren) {
            graphWalker.iterate(node)
        }
    }
}
