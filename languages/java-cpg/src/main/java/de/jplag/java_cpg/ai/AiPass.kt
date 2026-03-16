package de.jplag.java_cpg.ai

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.TranslationResult
import de.fraunhofer.aisec.cpg.graph.Component
import de.fraunhofer.aisec.cpg.graph.Node
import de.fraunhofer.aisec.cpg.graph.declarations.NamespaceDeclaration
import de.fraunhofer.aisec.cpg.graph.records
import de.fraunhofer.aisec.cpg.passes.TranslationResultPass
import de.fraunhofer.aisec.cpg.passes.configuration.DependsOn
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteBefore
import de.jplag.java_cpg.passes.CpgTransformationPass
import de.jplag.java_cpg.passes.TokenizationPass
import java.net.URI
import java.util.function.Consumer

/**
 * A CPG Pass performing Abstract Interpretation on the CPG Translation Result.
 */
@DependsOn(CpgTransformationPass::class)
@ExecuteBefore(TokenizationPass::class)
class AiPass(ctx: TranslationContext) : TranslationResultPass(ctx) {
    //passes have to be in kotlin!

    /**
     * Empty cleanup method.
     */
    override fun cleanup() {
        // Nothing to do
    }

    override fun accept(p0: TranslationResult) {
        var visitedLinesRecorder = VisitedLinesRecorder()
        AbstractInterpretation.setContinueOnError(continueOnError)
        val abstractInterpretation = AbstractInterpretation(visitedLinesRecorder, removeDeadCode)
        assert(p0.components.size == 1)
        val comp: Component = p0.components.first()
        for (translationUnit in comp.translationUnits) {
            if (translationUnit.name.parent?.localName?.endsWith("Main") == true || translationUnit.name.toString()
                    .endsWith("Main.java") || comp.translationUnits.size == 1
            ) {
                runCatching {
                    abstractInterpretation.runMain(translationUnit)
                }.onFailure { t ->
                    log.error("AI pass failed for translation unit: ${translationUnit.name}", t)
                    throw t
                }
            }
        }
        //find dead methods and classes
        try {
            for (translationUnit in comp.translationUnits) {
                for (recordDeclaration in translationUnit.records) {
                    if (checkIfCompletelyDead(recordDeclaration, visitedLinesRecorder) && removeDeadCode) {
                        // Try removing from Translation Unit directly
                        val tuIndex = translationUnit.declarations.indexOf(recordDeclaration)
                        if (tuIndex > 0) {
                            translationUnit.declarationEdges.removeAt(tuIndex)
                        }
                        // Try removing from Namespaces
                        for (ns in translationUnit.declarations.filterIsInstance<NamespaceDeclaration>()) {
                            val nsIndex = ns.declarations.indexOf(recordDeclaration)
                            if (nsIndex > 0) {
                                ns.declarations.removeAt(nsIndex)
                            }
                        }
                        continue
                    }
                    for (method in recordDeclaration.methods) {
                        if (checkIfCompletelyDead(method, visitedLinesRecorder) && removeDeadCode) {
                            if (method.name.localName == "toString" || method.name.localName == "equals" || method.name.localName == "hashCode"
                                || method.name.localName == "compareTo" || method.name.localName == "compare"
                            ) {
                                continue    //methods that are sometimes not visited by the AI but could still be called implicitly
                                //this is only a last resort as methods called inside these methods may still incorrectly be detected as dead code
                            }
                            val index = recordDeclaration.methods.indexOf(method)
                            recordDeclaration.methodEdges.removeAt(index)
                        }
                    }
                    //inner classes
                    for (innerClass in recordDeclaration.records) {
                        if (checkIfCompletelyDead(innerClass, visitedLinesRecorder) && removeDeadCode) {
                            val index = recordDeclaration.records.indexOf(innerClass)
                            recordDeclaration.recordEdges.removeAt(index)
                            innerClass.disconnectFromGraph()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            log.error("Error while detecting dead classes and methods", e)
        }
        deadLinesCallback!!.accept(visitedLinesRecorder.deadLinesCount)
        deadCountCallback!!.accept(visitedLinesRecorder.deadCodeCount)
    }

    fun checkIfCompletelyDead(node: Node, visitedLinesRecorder: VisitedLinesRecorder): Boolean {
        val fileName: URI = node.location?.artifactLocation?.uri ?: URI.create("unknown")
        val startLine: Int = node.location?.region?.startLine ?: -1
        val endLine: Int = node.location?.region?.endLine ?: -1
        val completelyDead: Boolean = visitedLinesRecorder.checkIfCompletelyDead(fileName, startLine, endLine)
        if (completelyDead) {
            visitedLinesRecorder.recordDetectedDeadLines(fileName, startLine, endLine)
        }
        return completelyDead
    }

    companion object AiPassCompanion {
        var removeDeadCode: Boolean = true
        var continueOnError: Boolean = false
        var deadLinesCallback: Consumer<Int>? = null
        var deadCountCallback: Consumer<Int>? = null
    }

}

/**
 * Enumeration of different representations for integers.
 */
enum class IntAiType {
    INTERVALS,
    DEFAULT,
    SET,
}

/**
 * Enumeration of different representations for floating-point numbers.
 */
enum class FloatAiType {
    SET,
    DEFAULT,
}

/**
 * Enumeration of different representations for strings.
 */
enum class StringAiType {
    CHAR_INCLUSION,
    REGEX,
    DEFAULT,
}

/**
 * Enumeration of different representations for characters.
 */
enum class CharAiType {
    SET,
    DEFAULT,
}

/**
 * Enumeration of different representations for arrays.
 */
enum class ArrayAiType {
    LENGTH,
    DEFAULT,
}
