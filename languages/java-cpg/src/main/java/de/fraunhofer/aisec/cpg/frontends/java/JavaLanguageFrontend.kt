/*
 * Copyright (c) 2019, Fraunhofer AISEC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *                    $$$$$$\  $$$$$$$\   $$$$$$\
 *                   $$  __$$\ $$  __$$\ $$  __$$\
 *                   $$ /  \__|$$ |  $$ |$$ /  \__|
 *                   $$ |      $$$$$$$  |$$ |$$$$\
 *                   $$ |      $$  ____/ $$ |\_$$ |
 *                   $$ |  $$\ $$ |      $$ |  $$ |
 *                   \$$$$$   |$$ |      \$$$$$   |
 *                    \______/ \__|       \______/
 *
 */
package de.fraunhofer.aisec.cpg.frontends.java

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.Problem
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.Node.Parsedness
import com.github.javaparser.ast.comments.Comment
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations
import com.github.javaparser.ast.nodeTypes.NodeWithType
import com.github.javaparser.ast.type.*
import com.github.javaparser.resolution.UnsolvedSymbolException
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration
import com.github.javaparser.resolution.types.ResolvedArrayType
import com.github.javaparser.resolution.types.ResolvedPrimitiveType
import com.github.javaparser.resolution.types.ResolvedReferenceType
import com.github.javaparser.resolution.types.ResolvedType
import com.github.javaparser.resolution.types.ResolvedVoidType
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.frontends.Language
import de.fraunhofer.aisec.cpg.frontends.LanguageFrontend
import de.fraunhofer.aisec.cpg.frontends.TranslationException
import de.fraunhofer.aisec.cpg.graph.*
import de.fraunhofer.aisec.cpg.graph.Annotation
import de.fraunhofer.aisec.cpg.graph.declarations.NamespaceDeclaration
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration
import de.fraunhofer.aisec.cpg.graph.scopes.Scope
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression
import de.fraunhofer.aisec.cpg.helpers.Benchmark
import de.fraunhofer.aisec.cpg.helpers.CommonPath
import de.fraunhofer.aisec.cpg.passes.JavaExternalTypeHierarchyResolver
import de.fraunhofer.aisec.cpg.passes.JavaImportResolver
import de.fraunhofer.aisec.cpg.passes.configuration.RegisterExtraPass
import de.fraunhofer.aisec.cpg.sarif.PhysicalLocation
import de.fraunhofer.aisec.cpg.sarif.Region
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull

/** Main parser for ONE Java file. */
@RegisterExtraPass(
    JavaExternalTypeHierarchyResolver::class
) // this pass is always required for Java
@RegisterExtraPass(JavaImportResolver::class)
open class JavaLanguageFrontend(language: Language<JavaLanguageFrontend>, ctx: TranslationContext) :
    LanguageFrontend<Node, Type>(language, ctx) {

    var context: CompilationUnit? = null
    var javaSymbolResolver: JavaSymbolSolver?
    val nativeTypeResolver = CombinedTypeSolver()

    lateinit var expressionHandler: ExpressionHandler
    lateinit var statementHandler: StatementHandler
    lateinit var declarationHandler: DeclarationHandler

    init {
        setupHandlers()
    }

    private fun setupHandlers() {
        expressionHandler = ExpressionHandler(this)
        statementHandler = StatementHandler(this)
        declarationHandler = DeclarationHandler(this)
    }

    @Throws(TranslationException::class)
    override fun parse(file: File): TranslationUnitDeclaration {
        // load in the file
        return try {
            val parserConfiguration = ParserConfiguration()
            parserConfiguration.setSymbolResolver(javaSymbolResolver)
            val parser = JavaParser(parserConfiguration)

            // parse the file
            var bench = Benchmark(this.javaClass, "Parsing source file")

            context = parse(file, parser)
            bench.addMeasurement()
            bench = Benchmark(this.javaClass, "Transform to CPG")
            context?.setData(Node.SYMBOL_RESOLVER_KEY, javaSymbolResolver)

            // starting point is always a translation declaration
            val fileDeclaration = newTranslationUnitDeclaration(file.toString(), rawNode = context)
            currentTU = fileDeclaration
            scopeManager.resetToGlobal(fileDeclaration)
            val packDecl = context?.packageDeclaration?.orElse(null)
            var namespaceDeclaration: NamespaceDeclaration? = null
            if (packDecl != null) {
                namespaceDeclaration =
                    newNamespaceDeclaration(packDecl.name.asString(), rawNode = packDecl)
                scopeManager.addDeclaration(namespaceDeclaration)
                scopeManager.enterScope(namespaceDeclaration)
            }

            for (type in context?.types ?: listOf()) {
                // handle each type. all declaration in this type will be added by the scope manager
                // along
                // the way
                val declaration = declarationHandler.handle(type)
                scopeManager.addDeclaration(declaration)
            }

            for (anImport in context?.imports ?: listOf()) {
                val incl = newIncludeDeclaration(anImport.nameAsString)
                scopeManager.addDeclaration(incl)
            }

            if (namespaceDeclaration != null) {
                scopeManager.leaveScope(namespaceDeclaration)
            }
            bench.addMeasurement()
            fileDeclaration
        } catch (ex: IOException) {
            throw TranslationException(ex)
        }
    }

    @Throws(TranslationException::class, FileNotFoundException::class)
    fun parse(file: File?, parser: JavaParser): CompilationUnit {
        val result = parser.parse(file)
        val optional = result.result
        if (optional.isEmpty) {
            throw TranslationException("JavaParser could not parse file")
        }
        if (optional.get().parsed == Parsedness.PARSED) {
            log.debug("Successfully parsed java file")
        } else {
            result.problems.forEach(
                Consumer { p: Problem ->
                    val sb = StringBuilder()
                    sb.append(p.message)
                    if (p.location.isPresent && p.location.get().begin.range.isPresent) {
                        sb.append(" ")
                        sb.append(p.location.get().begin.range.get().begin.toString())
                    }
                    log.error(sb.toString())
                }
            )
            log.error("Could not parse the file {} correctly! AST may be empty", file)
        }
        return optional.get()
    }

    override fun codeOf(astNode: Node): String? {
        val optional = astNode.tokenRange
        if (optional?.isPresent == true) {
            return optional.get().toString()
        }
        return astNode.toString()
    }

    override fun locationOf(astNode: Node): PhysicalLocation? {
        // find compilation unit of node
        val cu = astNode.findCompilationUnit().orElse(null) ?: return null

        // retrieve storage
        val storage = cu.storage.orElse(null) ?: return null
        val optional = astNode.range
        if (optional.isPresent) {
            val r = optional.get()
            val region =
                Region(
                    r.begin.line,
                    r.begin.column,
                    r.end.line,
                    r.end.column + 1
                ) // +1 for SARIF compliance
            return PhysicalLocation(storage.path.toUri(), region)
        }
        return null
    }

    fun <N : Node, T : Type> getTypeAsGoodAsPossible(
        nodeWithType: NodeWithType<N, T>,
        resolved: ResolvedValueDeclaration
    ): de.fraunhofer.aisec.cpg.graph.types.Type {
        return try {
            val type = nodeWithType.typeAsString
            if (type == "var") {
                unknownType()
            } else typeOf(resolved.type)
        } catch (ex: RuntimeException) {
            getTypeFromImportIfPossible(nodeWithType.type)
        } catch (ex: NoClassDefFoundError) {
            getTypeFromImportIfPossible(nodeWithType.type)
        }
    }

    fun getTypeAsGoodAsPossible(type: Type): de.fraunhofer.aisec.cpg.graph.types.Type {
        return try {
            if (type.toString() == "var") {
                unknownType()
            } else typeOf(type.resolve())
        } catch (ex: RuntimeException) {
            getTypeFromImportIfPossible(type)
        } catch (ex: NoClassDefFoundError) {
            getTypeFromImportIfPossible(type)
        }
    }

    // TODO: Return a Name instead of a String
    fun getQualifiedMethodNameAsGoodAsPossible(callExpr: MethodCallExpr): String {
        return try {
            callExpr.resolve().qualifiedName
        } catch (ex: RuntimeException) {
            val scope = callExpr.scope
            if (scope.isPresent) {
                val expression = scope.get()
                if (expression is NameExpr) {
                    // try to look for imports matching the name
                    // i.e. a static call
                    val fromImport = getQualifiedNameFromImports(callExpr.nameAsString)?.toString()
                    if (fromImport != null) {
                        return fromImport
                    }
                }
                if (scope.get().toString() == THIS) {
                    // this is not strictly true. This could also be a function of a superclass,
                    // but is the best we can do for now. If the superclass would be known,
                    // this would already be resolved by the Java resolver
                    fqn(callExpr.nameAsString).toString()
                } else {
                    scope.get().toString() + "." + callExpr.nameAsString
                }
            } else {
                // if the method is a static method of a resolvable class, the .resolve() would have
                // worked. but, the following can still be false, if the superclass implements
                // callExpr, but
                // is not available for analysis

                // check if this is a "specific" static import (not of the type 'import static
                // x.y.Z.*')
                val fromImport = getQualifiedNameFromImports(callExpr.nameAsString)?.toString()
                fromImport ?: fqn(callExpr.nameAsString).toString()
                // this is not strictly true. This could also be a function of a superclass or from
                // a static asterisk import
            }
        } catch (ex: NoClassDefFoundError) {
            val scope = callExpr.scope
            if (scope.isPresent) {
                val expression = scope.get()
                if (expression is NameExpr) {
                    val fromImport = getQualifiedNameFromImports(callExpr.nameAsString)?.toString()
                    if (fromImport != null) {
                        return fromImport
                    }
                }
                if (scope.get().toString() == THIS) {
                    fqn(callExpr.nameAsString).toString()
                } else {
                    scope.get().toString() + "." + callExpr.nameAsString
                }
            } else {
                val fromImport = getQualifiedNameFromImports(callExpr.nameAsString)?.toString()
                fromImport ?: fqn(callExpr.nameAsString).toString()
            }
        }
    }

    fun recoverTypeFromUnsolvedException(ex: Throwable): String? {
        if (
            ex is UnsolvedSymbolException || ex.cause != null && ex.cause is UnsolvedSymbolException
        ) {
            val qualifier: String? =
                if (ex is UnsolvedSymbolException) {
                    ex.name
                } else {
                    (ex.cause as UnsolvedSymbolException?)?.name
                }
            // this comes from the JavaParser!
            if (
                qualifier == null ||
                    qualifier.startsWith("We are unable to find") ||
                    qualifier.startsWith("Solving ")
            ) {
                return null
            }
            val fromImport = getQualifiedNameFromImports(qualifier)?.toString()
            return fromImport ?: getFQNInCurrentPackage(qualifier)
        }
        log.debug("Unable to resolve qualified name from exception")
        return null
    }

    fun getQualifiedNameFromImports(className: String?): Name? {
        if (context != null && className != null) {
            val name = parseName(className)

            // See if we can make the qualifier more precise using the imports
            for (importDeclaration in context?.imports ?: listOf()) {
                // Skip asterisk imports, otherwise the name comparison below will get confused
                // and we are looking for directly imported classes here only
                if (importDeclaration.isAsterisk) {
                    continue
                }

                val importName = parseName(importDeclaration.nameAsString)
                if (importName.endsWith(name)) {
                    return importName
                }
            }
        }

        return null
    }

    fun <N : Node, T : Type> getReturnTypeAsGoodAsPossible(
        nodeWithType: NodeWithType<N, T>,
        resolved: ResolvedMethodDeclaration
    ): de.fraunhofer.aisec.cpg.graph.types.Type {
        return try {
            // Resolve type first with ParameterizedType
            var type: de.fraunhofer.aisec.cpg.graph.types.Type? =
                typeManager.getTypeParameter(
                    scopeManager.currentRecord,
                    resolved.returnType.describe()
                )
            if (type == null) {
                type = typeOf(resolved.returnType)
            }
            type
        } catch (ex: RuntimeException) {
            getTypeFromImportIfPossible(nodeWithType.type)
        } catch (ex: NoClassDefFoundError) {
            getTypeFromImportIfPossible(nodeWithType.type)
        }
    }

    /**
     * Returns the FQN of the given parameter assuming that is declared somewhere in the same
     * package. Names declared in a package are automatically imported.
     *
     * @param simpleName the simple name
     * @return the FQN
     */
    private fun getFQNInCurrentPackage(simpleName: String): String {
        // TODO: Somehow we cannot use scopeManager.currentNamespace. not sure why
        val theScope =
            scopeManager.firstScopeOrNull { scope: Scope -> scope.astNode is NamespaceDeclaration }
                ?: return simpleName
        // If scope is null we are in a default package
        return theScope.name?.fqn(simpleName).toString()
    }

    private fun getTypeFromImportIfPossible(type: Type): de.fraunhofer.aisec.cpg.graph.types.Type {
        var searchType = type
        while (searchType.isArrayType) {
            searchType = searchType.elementType
        }
        // if this is not a ClassOrInterfaceType, just return
        if (!searchType.isClassOrInterfaceType || context == null) {
            log.warn("Unable to resolve type for {}", type.asString())
            val returnType = this.typeOf(type)
            returnType.typeOrigin = de.fraunhofer.aisec.cpg.graph.types.Type.Origin.GUESSED
            return returnType
        }
        val clazz = searchType.asClassOrInterfaceType()
        if (clazz != null) {
            // try to look for imports matching the name
            for (importDeclaration in context?.imports ?: listOf()) {
                if (importDeclaration.name.identifier.endsWith(clazz.name.identifier)) {
                    // TODO: handle type parameters
                    return objectType(importDeclaration.nameAsString)
                }
            }
            val returnType = this.typeOf(clazz)

            // no import found, so our last guess is that the type is in the same package
            // as our current translation unit, so we can "adjust" the name to an FQN
            val o = context?.packageDeclaration
            if (o?.isPresent == true) {
                returnType.name =
                    parseName(o.get().nameAsString + language.namespaceDelimiter + returnType.name)
            }

            returnType.typeOrigin = de.fraunhofer.aisec.cpg.graph.types.Type.Origin.GUESSED
            return returnType
        }
        log.warn("Unable to resolve type for {}", type.asString())
        val returnType = this.typeOf(type)
        returnType.typeOrigin = de.fraunhofer.aisec.cpg.graph.types.Type.Origin.GUESSED
        return returnType
    }

    override fun cleanup() {
        // FIXME: Remove this CPG 8.3.0 overlay once cache clearing shares JavaParserFacade.get's monitor upstream.
        synchronized(JavaParserFacade::class.java) {
            JavaParserFacade.clearInstances()
        }
        super.cleanup()

        context = null
    }

    override fun setComment(node: de.fraunhofer.aisec.cpg.graph.Node, astNode: Node) {
        astNode.comment.ifPresent { comment: Comment -> node.comment = comment.content }
    }

    /**
     * Processes Java annotations.
     *
     * @param node the node
     * @param owner the AST owner node
     */
    fun processAnnotations(
        node: de.fraunhofer.aisec.cpg.graph.Node,
        owner: NodeWithAnnotations<*>
    ) {
        if (config.processAnnotations) {
            node.addAnnotations(handleAnnotations(owner))
        }
    }

    private fun handleAnnotations(owner: NodeWithAnnotations<*>): List<Annotation> {
        val list = ArrayList<Annotation>()
        for (expr in owner.annotations) {
            val annotation = newAnnotation(expr.nameAsString, rawNode = expr)
            val members = ArrayList<AnnotationMember>()

            // annotations can be specified as member / value pairs
            if (expr.isNormalAnnotationExpr) {
                for (pair in expr.asNormalAnnotationExpr().pairs) {
                    val member =
                        newAnnotationMember(
                            pair.nameAsString,
                            expressionHandler.handle(pair.value) as Expression,
                            rawNode = pair.value
                        )
                    members.add(member)
                }
            } else if (expr.isSingleMemberAnnotationExpr) {
                val value = expr.asSingleMemberAnnotationExpr().memberValue
                if (value != null) {
                    // or as a literal. in this case it is assigned to the annotation member 'value'
                    val member =
                        newAnnotationMember(
                            ANNOTATION_MEMBER_VALUE,
                            expressionHandler.handle(value) as Expression,
                            rawNode = value
                        )
                    members.add(member)
                }
            }
            annotation.members = members
            list.add(annotation)
        }
        return list
    }

    override fun typeOf(type: Type): de.fraunhofer.aisec.cpg.graph.types.Type {
        return when (type) {
            is ArrayType -> this.typeOf(type.elementType).array()
            is VoidType -> incompleteType()
            is PrimitiveType -> primitiveType(type.asString())
            is ClassOrInterfaceType ->
                objectType(
                    type.nameWithScope,
                    type.typeArguments.getOrNull()?.map { this.typeOf(it) } ?: listOf()
                )
            is ReferenceType -> objectType(type.asString())
            else -> objectType(type.asString())
        }
    }

    fun typeOf(type: ResolvedType): de.fraunhofer.aisec.cpg.graph.types.Type {
        return when (type) {
            is ResolvedArrayType -> typeOf(type.componentType).array()
            is ResolvedVoidType -> incompleteType()
            is ResolvedPrimitiveType -> primitiveType(type.describe())
            is ResolvedReferenceType ->
                objectType(type.describe(), type.typeParametersValues().map { typeOf(it) })
            else -> objectType(type.describe())
        }
    }

    companion object {
        const val THIS = "this"
        const val ANNOTATION_MEMBER_VALUE = "value"
    }

    init {
        val reflectionTypeSolver = ReflectionTypeSolver()
        nativeTypeResolver.add(reflectionTypeSolver)
        var root = config.topLevel
        if (root == null && config.softwareComponents.size == 1) {
            root =
                config.softwareComponents[config.softwareComponents.keys.first()]?.let {
                    CommonPath.commonPath(it)
                }
        }
        if (root == null) {
            log.warn("Could not determine source root for {}", config.softwareComponents)
        } else {
            log.info("Source file root used for type solver: {}", root)
            val javaParserTypeSolver = JavaParserTypeSolver(root)
            nativeTypeResolver.add(javaParserTypeSolver)
        }
        javaSymbolResolver = JavaSymbolSolver(nativeTypeResolver)
    }
}
