package de.jplag.java_cpg.passes

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.Component
import de.fraunhofer.aisec.cpg.graph.declarations.Declaration
import de.fraunhofer.aisec.cpg.graph.declarations.FieldDeclaration
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration
import de.fraunhofer.aisec.cpg.graph.fields
import de.fraunhofer.aisec.cpg.graph.methods
import de.fraunhofer.aisec.cpg.graph.refs
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberExpression
import de.fraunhofer.aisec.cpg.graph.types.Type
import de.fraunhofer.aisec.cpg.graph.types.recordDeclaration
import de.fraunhofer.aisec.cpg.passes.ComponentPass
import de.fraunhofer.aisec.cpg.passes.DynamicInvokeResolver
import de.fraunhofer.aisec.cpg.passes.order.DependsOn
import org.slf4j.LoggerFactory

/**
 * This pass associates record member references with superclass member definitions, if these member references have no
 * other definition associated to them.
 */
@DependsOn(DynamicInvokeResolver::class)
class FixAstPass(ctx: TranslationContext) : ComponentPass(ctx) {
    private val logger = LoggerFactory.getLogger(FixAstPass::class.java)

    override fun accept(t: Component) {
        val defectiveReferences = t.refs
            .filterIsInstance<MemberExpression>()
            .filter { it.refersTo != null && it.refersTo?.location == null }
            .map { Pair(it.refersTo!!, it) }
            .let { logger.info("Found ${it.size} defective field references."); it }
            .groupBy { it.first.javaClass.simpleName }

        val superFieldReferences : List<Pair<Declaration, MemberExpression>>? = defectiveReferences["FieldDeclaration"]
        val successFields = superFieldReferences?.associateWith { fixSuperFieldReference(it.first as FieldDeclaration, it.second) }
            ?.count { it.value } ?: 0

        val superMethodReferences : List<Pair<Declaration, MemberExpression>>? = defectiveReferences["MethodDeclaration"]
        val successMethods = superMethodReferences?.associateWith { fixSuperMethodReference(it.first as MethodDeclaration, it.second) }
            ?.count { it.value } ?: 0
        logger.info("Fixed $successFields fields and $successMethods methods, sum: ${successFields + successMethods}.")
    }

    private fun fixSuperFieldReference(field: FieldDeclaration, ref: MemberExpression): Boolean {
        return fixReference(ref, field) { it.recordDeclaration.fields }
    }

    private fun fixSuperMethodReference(method: MethodDeclaration, ref: MemberExpression): Boolean {
        return fixReference(ref, method) { it.recordDeclaration.methods }
    }

    private fun <T: Declaration> fixReference(
        ref: MemberExpression,
        method: T,
        getCandidates: (Type) -> List<T>
    ): Boolean {
        val subClassType = ref.base.type;
        val superTypes = mutableListOf<Type>()
        superTypes.add(subClassType)

        var match: T? = null
        while (superTypes.isNotEmpty()) {
            val superType = superTypes.removeFirst()
            match = getCandidates(superType).find { it.name.localName == method.name.localName }
            if (match != null) {
                break
            }
            superTypes.addAll(superType.superTypes)
        }

        if (match == null) {
            return false
        }
        ref.refersTo = match
        return true
    }

    override fun cleanup() {

    }


}