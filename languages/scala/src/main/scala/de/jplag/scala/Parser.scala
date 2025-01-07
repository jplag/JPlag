package de.jplag.scala

import ScalaTokenAttribute._
import de.jplag.util.FileUtils
import de.jplag.{AbstractParser, Language, LanguageLoader, ParsingException, Token}

import java.io.File
import java.util.stream.Collectors
import scala.collection.mutable.ListBuffer
import scala.meta.Member.ParamClauseGroup
import scala.meta._

class Parser(private val language: Language) extends AbstractParser {

    private var currentFile: File = _

    private var tokens: ListBuffer[Token] = _

    private val traverser: Traverser = new Traverser {

        private val Operators: Array[String] = Array(
            "+", "-", "*", "/", "%", "**",
            "==", "!=", ">", "<", ">=", "<=",
            "&&", "||", "!",
            "=", "+=", "-=", "*=", "/=", "%=", "**=", "<<=", ">>=", ">>>=", "&=", "^=", "|=",
            "&", "|", "^", "<<", ">>", "~", ">>>",
            "++", "::", ":::",
            "<:", ">:", "#"
        )

        /**
         * TraverserRecord; Identifier kept short for readability
         *
         * @param before   opt. token type to insert at the beginning of the tree
         * @param after    opt. token type to insert at the end of the tree
         * @param traverse custom method of traversing the tree
         */
        private case class TR(before: Option[ScalaTokenAttribute] = None,
                              after: Option[ScalaTokenAttribute] = None,
                              traverse: Tree => Unit = _.children.foreach(traverser.apply)
                             )

        /**
         * Adds a token at the beginning of the tree and traverses it if a tree is given.
         *
         * @param tree  opt. tree
         * @param token token type to insert at the beginning of the tree
         */
        private def maybeAddAndApply(tree: Option[Tree], token: ScalaTokenAttribute): Unit = tree match {
            case Some(expression) =>
                add(token, expression, fromEnd = false)
                apply(expression)
            case None =>
        }

        private def processCases(cases: List[Case]): Unit = cases.foreach {
            case caseTree@Case(pattern, condition, body) =>
                add(CASE_STATEMENT, caseTree, fromEnd = false)
                applyRecursively(Seq(pattern, condition))

                encloseAndApply(body, TR(Some(CASE_BEGIN), Some(CASE_END)))
            case _ =>
        }

        private def isStandardOperator(operator: String): Boolean = {
            Operators.contains(operator)
        }

        private def getMethodIdentifier(function: Term): String = {
            function.toString().split("\\.").last
        }

        private def handleDefinitionPattern(pattern: Pat, optionalValue: Option[Term]): Unit = {
            pattern match {
                // variable tuple
                case Pat.Tuple(patternArgs) =>
                    optionalValue match {
                        // not initialized
                        case None => for (elem <- patternArgs) handleDefinitionPattern(elem, None)

                        // initialized with tuple literal
                        case Some(Term.Tuple(values)) => patternArgs.zip(values).foreach {
                            case (pattern, value) => handleDefinitionPattern(pattern, Some(value))
                        }

                        // initialized with tuple reference
                        case Some(value) =>
                            for (elem <- patternArgs) handleDefinitionPattern(elem, None)
                            add(ASSIGN, value, fromEnd = false)
                    }

                // single variable
                case other@_ =>
                    add(VARIABLE_DEFINITION, other, fromEnd = false)
                    apply(other)
                    maybeAddAndApply(optionalValue, ASSIGN)
            }
        }

        private def doMatch(tree: Tree): TR = {
            tree match {
                case Term.Do(body, condition) => TR(Some(DO_WHILE), Some(DO_WHILE_END), traverse = _ => {
                    encloseAndApply(body, TR(Some(DO_BODY_BEGIN), Some(DO_BODY_END)))
                    apply(condition)
                })
                case Term.Assign(_) => TR(Some(ASSIGN))
                case Term.While(condition, body) => TR(Some(WHILE), traverse = _ => {
                    apply(condition)
                    encloseAndApply(body, TR(Some(WHILE_BODY_BEGIN), Some(WHILE_BODY_END)))
                })
                case Term.For(enumerators, body) =>
                    TR(Some(FOR), traverse = _ => {
                        apply(enumerators)
                        encloseAndApply(body, TR(Some(FOR_BODY_BEGIN), Some(FOR_BODY_END)))
                    })
                case Term.Try(expression, catchExpression, finallyExpression) => TR(Some(TRY_BEGIN),
                    traverse = _ => {
                        apply(expression)
                        if (catchExpression.nonEmpty) {
                            val start = catchExpression.head.pos
                            val end = catchExpression.last.pos
                            val length = end.endLine - start.startLine

                            add(CATCH_BEGIN, start.startLine, start.startColumn, length)
                            processCases(catchExpression)
                            add(CATCH_END, end.endLine, end.endColumn, length)
                        }

                        maybeAddAndApply(finallyExpression, FINALLY)
                    })
                case Term.TryWithHandler(expression, catchExpression, finallyExpression) => TR(Some(TRY_BEGIN),
                    traverse = _ => {
                        apply(expression)
                        encloseAndApply(catchExpression, TR(Some(CATCH_BEGIN), Some(CATCH_END)))

                        maybeAddAndApply(finallyExpression, FINALLY)
                    })
                case call: Term.Apply if !isStandardOperator(getMethodIdentifier(call.fun)) && call.argClause.nonEmpty =>
                    // function calls with no arguments are not covered here; see README
                    val function = call.fun
                    val arguments = call.argClause
                    TR(traverse = _ => {

                        add(APPLY, function, fromEnd = false)
                        apply(function)
                        for (argument <- arguments) {
                            add(ARGUMENT, argument, fromEnd = false)
                            argument match {
                                case Term.Assign(assignee, value) =>
                                    // default values
                                    apply(assignee)
                                    apply(value)
                                case _ => apply(argument)
                            }
                        }
                    })
                case Term.NewAnonymous(_) => TR(Some(NEW_CREATION_BEGIN), Some(NEW_CREATION_END))
                case Term.Return(_) => TR(Some(RETURN))
                case matchTerm: Term.Match => TR(Some(MATCH_BEGIN), Some(MATCH_END), traverse = _ => {
                    apply(matchTerm.expr)
                    processCases(matchTerm.cases)
                })
                case Term.Throw(_) => TR(Some(THROW))
                case _: Term.Function => TR(Some(FUNCTION_BEGIN), Some(FUNCTION_END))
                case Term.PartialFunction(cases) => TR(Some(PARTIAL_FUNCTION_BEGIN), Some(PARTIAL_FUNCTION_END), traverse = _ => {
                    processCases(cases)
                })
                case Term.ForYield(enumerators, body) => TR(traverse = _ => {
                    apply(enumerators)
                    add(FOR_BODY_BEGIN, body, fromEnd = false)
                    encloseAndApply(body, TR(Some(YIELD), Some(FOR_BODY_END)))
                })
                case ifTerm: Term.If => TR(traverse = _ => {
                    val condition = ifTerm.cond
                    val thenExpression = ifTerm.thenp
                    val elseExpression = ifTerm.elsep

                    add(IF, tree, fromEnd = false)
                    apply(condition)

                    encloseAndApply(thenExpression, TR(Some(IF_BEGIN), Some(IF_END)))

                    elseExpression match {
                        case Lit.Unit() => apply(elseExpression)
                        case _ =>
                            val elseStart = tree.pos.text.indexOf("else", thenExpression.pos.end - tree.pos.start)
                            val elsePosition = Position.Range(tree.pos.input, tree.pos.start + elseStart, tree.pos.start + elseStart + 4)
                            add(ELSE, elsePosition.startLine + 1, elsePosition.startColumn + 1, elsePosition.text.length)
                            encloseAndApply(elseExpression, TR(Some(ELSE_BEGIN), Some(ELSE_END)))
                    }
                })

                case scala.meta.Pkg(_) => TR(Some(PACKAGE))
                case scala.meta.Import(_) => TR(Some(IMPORT))

                case definition: Defn.Def =>
                    TR(traverse = _ => {
                        applyRecursively(definition.mods)
                        add(METHOD_DEF, definition.name, fromEnd = false)
                        assignRecursively(getTParams(definition.paramClauseGroups), TYPE_PARAMETER)
                        assignRecursively(getPParamsLists(definition.paramClauseGroups), PARAMETER)

                        encloseAndApply(definition.body, TR(Some(METHOD_BEGIN), Some(METHOD_END)))
                    })
                case macroDef: Defn.Macro => TR(Some(MACRO), traverse = _ => {
                    applyRecursively(Seq(macroDef.mods, getTParams(macroDef.paramClauseGroups), getPParamsLists(macroDef.paramClauseGroups)))
                    encloseAndApply(macroDef.body, TR(Some(MACRO_BEGIN), Some(MACRO_END)))
                })
                case _: Defn.Class =>
                    TR(Some(CLASS_BEGIN), Some(CLASS_END))
                case Defn.Object(_) => TR(Some(OBJECT_BEGIN), Some(OBJECT_END))
                case _: Defn.Trait => TR(Some(TRAIT_BEGIN), Some(TRAIT_END))
                case _: Defn.Type => TR(Some(TYPE))
                case varDef: Defn.Var => TR(traverse = _ => {
                    apply(varDef.mods)
                    for (pattern <- varDef.pats) {
                        handleDefinitionPattern(pattern, Some(varDef.body))
                    }
                    apply(varDef.decltpe)
                })
                case Defn.Val(modifiers, patterns, declaredType, value) => TR(traverse = _ => {
                    apply(modifiers)
                    for (pattern <- patterns) {
                        handleDefinitionPattern(pattern, Some(value))
                    }
                    apply(declaredType)

                })

                case Decl.Var(_) => TR(Some(VARIABLE_DEFINITION))
                case Decl.Val(_) => TR(Some(VARIABLE_DEFINITION))
                case _: Decl.Def => TR(Some(METHOD_BEGIN), Some(METHOD_END))
                case _: Decl.Type => TR(Some(TYPE))

                case _: Ctor.Secondary =>
                    TR(Some(CONSTRUCTOR_BEGIN), Some(CONSTRUCTOR_END))

                case init: Init if getArgLists(init.argClauses).nonEmpty => TR(traverse = _ => {
                    assignRecursively(getArgLists(init.argClauses), ARGUMENT, doApply = true)
                })
                case Enumerator.Guard(_) => TR(Some(GUARD))

                case Term.Param(_) => TR(traverse = _ => add(PARAMETER, tree, fromEnd = false))
                case term: Term.ApplyInfix if term.op.value.contains("=") && !Array("==", "!=").contains(term.op.value) => TR(Some(ASSIGN))
                case term: Term.ApplyInfix if !isStandardOperator(term.op.value) => TR(traverse = _ => {
                    add(APPLY, tree, fromEnd = false)
                    apply(term.lhs)
                    assignRecursively(term.targClause.values, TYPE_ARGUMENT, doApply = true)
                    assignRecursively(term.argClause.values, ARGUMENT, doApply = true)
                })
                case Term.Select(refObj, member) =>
                    TR(traverse = _ => {
                        apply(refObj)
                        if (!isStandardOperator(member.value)) add(MEMBER, member, fromEnd = false)
                        apply(member)
                    })
                case term: Term.ApplyType => TR(traverse = _ => {
                    add(APPLY, tree, fromEnd = false)
                    assignRecursively(term.targClause.values, TYPE_ARGUMENT)
                })
                case Term.New(_) => TR(Some(NEW_OBJECT))
                case Self(_) => TR(Some(SELF_TYPE))
                case block@Term.Block(_) => block.parent.get.parent match {
                    // inner block
                    case _: Some[Term.Apply] => TR(Some(BLOCK_START), Some(BLOCK_END))
                    // block in an expression context, e.g. for, if, while
                    case _ => TR()
                }
                case Enumerator.Generator(_) => TR(Some(ENUM_GENERATOR))
                case _: meta.Type.Param => TR(Some(TYPE_PARAMETER))

                case _ => TR()
            }
        }

        /**
         * Traverses a sequence of elements, which may in turn be lists as well.
         *
         * @param elements a sequence of values, Trees or Lists
         * @tparam T the type of elements
         */
        private def applyRecursively[T](elements: Seq[T]): Unit = elements.foreach {
            case tree: Tree => apply(tree)
            case treeList: List[_] => applyRecursively(treeList)
            case _ =>
        }

        override def apply(tree: Tree): Unit = {
            val record: TR = doMatch(tree)

            record.before match {
                case Some(value) => add(value, tree, fromEnd = false)
                case None =>
            }

            record.traverse(tree)

            record.after match {
                case Some(value) => add(value, tree, fromEnd = true)
                case None =>
            }
        }

        /**
         * Assigns the same token type to each element of a list of e.g. parameters.
         *
         * @param els       list of equal elements
         * @param tokenType type of token to assign to each of the elements
         * @param doApply   if true, the substructure of each element is traversed
         * @tparam T list may contain more lists or leaves
         */
        private def assignRecursively[T](els: List[T], tokenType: ScalaTokenAttribute, doApply: Boolean = false): Unit = els.foreach {
            case treeList: List[_] => assignRecursively(treeList, tokenType)
            case el: Tree =>
                add(tokenType, el, fromEnd = false)
                if (doApply) apply(el)
        }

        /**
         * Adds a token to the beginning of the tree, traverses the tree, adds a token at the end of the tree.
         * The token types are given by record.
         *
         * @param tree   Tree to enclose and traverse
         * @param record contains token types
         */
        private def encloseAndApply(tree: Tree, record: TR): Unit = {
            record.before match {
                case Some(value) => add(value, tree, fromEnd = false)
                case None =>
            }

            apply(tree)

            record.after match {
                case Some(value) => add(value, tree, fromEnd = true)
                case None =>
            }
        }
    }

    def parse(files: Set[File]): List[Token] = {
        tokens = ListBuffer()
        for (file <- files) {
            parseFile(file)
            System.gc()
        }

        tokens.toList
    }

    private def parseFile(file: File) = {
        currentFile = file

        try {
            val text = FileUtils.readFileContent(file)
            val input = Input.VirtualFile(file.getPath, text)
            val ast = input.parse[Source].get
            traverser(ast)

            tokens += Token.fileEnd(currentFile)
        } catch {
            case exception: Throwable =>
                exception.printStackTrace()
                throw new ParsingException(file, exception.getMessage, exception)
        }
    }

    /**
     * Adds a token to the token list.
     *
     * @param tokenType the type of the token
     * @param line      line of the occurrence in the file
     * @param column    column of the occurrence in the file
     * @param length    length of the occurrence in the file
     */
    private def add(tokenType: ScalaTokenAttribute, line: Int, column: Int, length: Int): Unit = {
        tokens += new Token(tokenType, currentFile, line, column, length, language)
    }


    /**
     * Adds a token to the token list.
     *
     * @param tokenType the type of the token
     * @param node      the tree that marks the occurrence
     * @param fromEnd   if true, the token is added at the end of the tree (length 0).
     */
    private def add(tokenType: ScalaTokenAttribute, node: Tree, fromEnd: Boolean): Unit = {
        if (node.pos.text.nonEmpty) {
            // SELF type tokens with no text content mess up the sequence
            if (fromEnd) {
                tokens += new Token(tokenType, currentFile, node.pos.endLine + 1, node.pos.endColumn + 1, 0, language)
            }
            else {
                tokens += new Token(tokenType, currentFile, node.pos.startLine + 1, node.pos.endColumn + 1, node.pos.text.length, language)
            }
        }
    }

    private def getTParams(groups: List[ParamClauseGroup]): List[Type.Param] = {
        groups.flatMap(it => it.tparamClause.values)
    }

    private def getPParamsLists(groups: List[ParamClauseGroup]): List[List[Term.Param]] = {
        groups.flatMap(it => it.paramClauses.map(clause => clause.values))
    }

    private def getArgLists(arguments: Seq[Term.ArgClause]): List[List[Term]] = {
        arguments.map(it => it.values).toList
    }
}