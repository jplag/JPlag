package de.jplag.java_cpg.ai

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.declarations.ConstructorDeclaration
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration
import de.fraunhofer.aisec.cpg.graph.methods
import de.fraunhofer.aisec.cpg.graph.statements.expressions.ConstructExpression
import de.fraunhofer.aisec.cpg.graph.types.FunctionType
import de.fraunhofer.aisec.cpg.graph.types.Type
import de.fraunhofer.aisec.cpg.passes.TranslationUnitPass
import de.fraunhofer.aisec.cpg.passes.configuration.DependsOn
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteBefore
import de.jplag.java_cpg.ai.variables.values.IValue
import de.jplag.java_cpg.ai.variables.values.JavaObject
import de.jplag.java_cpg.ai.variables.values.Value
import de.jplag.java_cpg.passes.CpgTransformationPass
import de.jplag.java_cpg.passes.TokenizationPass
import java.util.function.Consumer

@DependsOn(CpgTransformationPass::class)
@ExecuteBefore(TokenizationPass::class)
class AiMethodPass(ctx: TranslationContext) : TranslationUnitPass(ctx) {
    //passes have to be in kotlin!

    /**
     * Empty cleanup method.
     */
    override fun cleanup() {
        // Nothing to do
    }

    override fun accept(p0: TranslationUnitDeclaration) {
        val visitedLinesRecorder = VisitedLinesRecorder()
        val methods: List<MethodDeclaration> = p0.methods
        for (method in methods) {
            if (method.hasBody() && method !is ConstructorDeclaration) {
                analyseMethod(method, visitedLinesRecorder)
            }
        }
        deadLinesCallback!!.accept(visitedLinesRecorder.deadLinesCount)
        deadCountCallback!!.accept(visitedLinesRecorder.deadCodeCount)
    }

    fun analyseMethod(method: MethodDeclaration, visitedLinesRecorder: VisitedLinesRecorder) {
        val abstractInterpretation = AbstractInterpretation(visitedLinesRecorder, removeDeadCode)
        AbstractInterpretation.setContinueOnError(continueOnError)
        abstractInterpretation.setMethodAnalysisMode()
        val recordName: String = method.recordDeclaration?.name?.toString() ?: "Main"
        val javaObject = JavaObject(
            de.jplag.java_cpg.ai.variables.Type(
                de.jplag.java_cpg.ai.variables.Type.TypeEnum.OBJECT,
                recordName
            )
        )
        abstractInterpretation.setupObject(javaObject, "this")

        val paramVars = ArrayList<IValue>()
        for (paramDecl in method.parameters) {
            val type: Type = paramDecl.type
            val cpgType = de.jplag.java_cpg.ai.variables.Type.fromCpgType(type)
            var value: IValue
            if (cpgType == de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.OBJECT)) {
                val constructExpression = ConstructExpression()
                constructExpression.name = paramDecl.name
                constructExpression.type = type
                value = AbstractInterpretation.createNewObject(constructExpression)
            } else {
                value = Value.valueFactory(de.jplag.java_cpg.ai.variables.Type.fromCpgType(type))
            }
            paramVars.add(value)
        }
        runCatching {
            abstractInterpretation.runMethod(
                method.name.toString(), paramVars, method,
                de.jplag.java_cpg.ai.variables.Type.fromCpgType((method.type as FunctionType).returnTypes.first())
            )
        }.onFailure { t ->
            log.error("AI pass failed for method: ${method.name}", t)
            throw t
        }
    }

    companion object AiMethodPassCompanion {
        var removeDeadCode: Boolean = true
        var continueOnError: Boolean = false
        var deadLinesCallback: Consumer<Int>? = null
        var deadCountCallback: Consumer<Int>? = null
    }

}
