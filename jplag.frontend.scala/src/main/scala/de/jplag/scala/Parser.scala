package de.jplag.scala

import de.jplag.scala.ScalaTokenConstants._
import de.jplag.{AbstractParser, TokenList}

import java.io.File
import scala.meta._


class Parser extends AbstractParser {
    private var currentFile: String = _

    private var tokens: TokenList = _

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
        private case class TR(before: Option[ScalaTokenConstants.Value] = None,
                              after: Option[ScalaTokenConstants.Value] = None,
                              traverse: Tree => Unit = _.children.foreach(traverser.apply)
                             )

        /**
         * Adds a token at the beginning of the tree and traverses it if a tree is given.
         *
         * @param tree  opt. tree
         * @param token token type to insert at the beginning of the tree
         */
        private def maybeAddAndApply(tree: Option[Tree], token: ScalaTokenConstants.Value): Unit = tree match {
            case Some(expression) =>
                add(token, expression, fromEnd = false)
                apply(expression)
            case None =>
        }

        private def processCases(cases: List[Case]): Unit = cases.foreach {
            case caseTree@Case(pattern, condition, body) =>
                add(ScalaTokenConstants.CaseStatement, caseTree, fromEnd = false)
                applyRecursively(Seq(pattern, condition))

                encloseAndApply(body, TR(Some(CaseBegin), Some(CaseEnd)))
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
                case tuple@Pat.Tuple(patternArgs) =>
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
                            add(Assign, value, fromEnd = false)
                    }

                // single variable
                case other@_ =>
                    add(VariableDefinition, other, fromEnd = false)
                    apply(other)
                    maybeAddAndApply(optionalValue, Assign)
            }
        }

        private def doMatch(tree: Tree): TR = {
            tree match {
                case Term.Do(body, condition) => TR(Some(DoWhile), Some(DoWhileEnd), traverse = _ => {
                    encloseAndApply(body, TR(Some(DoBodyBegin), Some(DoBodyEnd)))
                    apply(condition)
                })
                case Term.Assign(_) => TR(Some(Assign))
                case Term.While(condition, body) => TR(Some(While), traverse = _ => {
                    apply(condition)
                    encloseAndApply(body, TR(Some(WhileBodyBegin), Some(WhileBodyEnd)))
                })
                case Term.For(enumerators, body) =>
                    TR(Some(For), traverse = _ => {
                        apply(enumerators)
                        encloseAndApply(body, TR(Some(ForBodyBegin), Some(ForBodyEnd)))
                    })
                case Term.Try(expression, catchExpression, finallyExpression) => TR(Some(TryBegin),
                    traverse = _ => {
                        apply(expression)
                        if (catchExpression.nonEmpty) {
                            val start = catchExpression.head.pos
                            val end = catchExpression.last.pos
                            val length = end.endLine - start.startLine

                            add(CatchBegin, start.startLine, start.startColumn, length)
                            processCases(catchExpression)
                            add(CatchEnd, end.endLine, end.endColumn, length)
                        }

                        maybeAddAndApply(finallyExpression, ScalaTokenConstants.Finally)
                    })
                case Term.TryWithHandler(expression, catchExpression, finallyExpression) => TR(Some(TryBegin),
                    traverse = _ => {
                        apply(expression)
                        encloseAndApply(catchExpression, TR(Some(CatchBegin), Some(CatchEnd)))

                        maybeAddAndApply(finallyExpression, ScalaTokenConstants.Finally)
                    })
                case Term.Apply(function, arguments) if !isStandardOperator(getMethodIdentifier(function)) && arguments.nonEmpty =>
                    // function calls with no arguments are not covered here; see README
                    TR(traverse = _ => {

                        add(Apply, function, fromEnd = false)
                        apply(function)
                        for (argument <- arguments) {
                            add(Argument, argument, fromEnd = false)
                            argument match {
                                case Term.Assign(assignee, value) =>
                                    // default values
                                    apply(assignee)
                                    apply(value)
                                case _ => apply(argument)
                            }
                        }
                    })
                case Term.NewAnonymous(_) => TR(Some(NewCreationBegin), Some(NewCreationEnd))
                case Term.Return(_) => TR(Some(ScalaTokenConstants.Return))
                case Term.Match(expression, cases) => TR(Some(MatchBegin), Some(MatchEnd), traverse = _ => {
                    apply(expression)
                    processCases(cases)
                })
                case Term.Throw(_) => TR(Some(Throw))
                case Term.Function(_) => TR(Some(FunctionBegin), Some(FunctionEnd))
                case Term.PartialFunction(cases) => TR(Some(PartialFunctionBegin), Some(PartialFunctionEnd), traverse = _ => {
                    processCases(cases)
                })
                case Term.ForYield(enumerators, body) => TR(traverse = _ => {
                    apply(enumerators)
                    add(ForBodyBegin, body, fromEnd = false)
                    encloseAndApply(body, TR(Some(Yield), Some(ForBodyEnd)))
                })
                case Term.If(condition, thenExpression, elseExpression) => TR(traverse = _ => {
                    add(If, tree, fromEnd = false)
                    apply(condition)

                    encloseAndApply(thenExpression, TR(Some(IfBegin), Some(IfEnd)))

                    elseExpression match {
                        case Lit.Unit() => apply(elseExpression)
                        case _ =>
                            val elseStart = tree.pos.text.indexOf("else", thenExpression.pos.end - tree.pos.start)
                            val elsePosition = Position.Range(tree.pos.input, tree.pos.start + elseStart, tree.pos.start + elseStart + 4)
                            add(Else, elsePosition.startLine + 1, elsePosition.startColumn + 1, elsePosition.text.length)
                            encloseAndApply(elseExpression, TR(Some(ElseBegin), Some(ElseEnd)))
                    }
                })

                case scala.meta.Pkg(_) => TR(Some(Package))
                case scala.meta.Import(_) => TR(Some(ScalaTokenConstants.Import))

                case Defn.Def(modifiers, name, typeParameters, parameterLists, _, body) =>
                    TR(traverse = _ => {
                        applyRecursively(modifiers)
                        add(MethodDef, name, fromEnd = false)
                        assignRecursively(typeParameters, TypeParameter)
                        assignRecursively(parameterLists, Parameter)

                        encloseAndApply(body, TR(Some(MethodBegin), Some(MethodEnd)))
                    })
                case Defn.Macro(modifiers, macroName, typeParameters, parameterLists, declaredType, body) => TR(Some(Macro), traverse = _ => {
                    applyRecursively(Seq(modifiers, typeParameters, parameterLists))
                    encloseAndApply(body, TR(Some(MacroBegin), Some(MacroEnd)))
                })
                case Defn.Class(_) =>
                    TR(Some(ClassBegin), Some(ClassEnd))
                case Defn.Object(_) => TR(Some(ObjectBegin), Some(ObjectEnd))
                case Defn.Trait(_) => TR(Some(TraitBegin), Some(TraitEnd))
                case Defn.Type(_) => TR(Some(ScalaTokenConstants.Type))
                case Defn.Var(modifiers, patterns, declaredType, optionalValue) => TR(traverse = _ => {
                    apply(modifiers)
                    for (pattern <- patterns) {
                        handleDefinitionPattern(pattern, optionalValue)
                    }
                    apply(declaredType)
                })
                case Defn.Val(modifiers, patterns, declaredType, value) => TR(traverse = _ => {
                    apply(modifiers)
                    for (pattern <- patterns) {
                        handleDefinitionPattern(pattern, Some(value))
                    }
                    apply(declaredType)

                })

                case Decl.Var(_) => TR(Some(VariableDefinition))
                case Decl.Val(_) => TR(Some(VariableDefinition))
                case Decl.Def(_) => TR(Some(MethodBegin), Some(MethodEnd))
                case Decl.Type(_) => TR(Some(ScalaTokenConstants.Type))

                case Ctor.Secondary(_) =>
                    TR(Some(ConstructorBegin), Some(ConstructorEnd))

                case Init(typeName, name, argumentLists) if argumentLists.nonEmpty => TR(traverse = _ => {
                    assignRecursively(argumentLists, Argument, doApply = true)
                })
                case Enumerator.Guard(_) => TR(Some(Guard))

                case Term.Param(_) => TR(traverse = _ => add(Parameter, tree, fromEnd = false))
                case Term.ApplyInfix(_, operator, _, _) if operator.value.contains("=") && !Array("==", "!=").contains(operator.value) => TR(Some(Assign))
                case Term.ApplyInfix(function, operator, typeArgs, arguments) if !isStandardOperator(operator.value) => TR(traverse = _ => {
                    add(Apply, tree, fromEnd = false)
                    apply(function)
                    assignRecursively(typeArgs, TypeArgument, doApply = true)
                    assignRecursively(arguments, Argument, doApply = true)
                })
                case Term.Select(refObj, member) =>
                    TR(traverse = _ => {
                        apply(refObj)
                        if (!isStandardOperator(member.value)) add(ScalaTokenConstants.Member, member, fromEnd = false)
                        apply(member)
                    })
                case Term.ApplyType(_, typeArgs) => TR(traverse = _ => {
                    add(Apply, tree, fromEnd = false)
                    assignRecursively(typeArgs, TypeArgument)
                })
                case Term.New(_) => TR(Some(NewObject))
                case Self(_) => TR(Some(SelfType))
                case block@Term.Block(_) => block.parent match {
                    // inner block
                    case Some(Term.Apply(_)) => TR(Some(BlockStart), Some(BlockEnd))
                    // block in an expression context, e.g. for, if, while
                    case _ => TR()
                }
                case Enumerator.Generator(_) => TR(Some(EnumGenerator))
                case meta.Type.Param(_) => TR(Some(TypeParameter))

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
        private def assignRecursively[T](els: List[T], tokenType: ScalaTokenConstants.Value, doApply: Boolean = false): Unit = els.foreach {
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

    def parse(directory: File, files: Array[String]): TokenList = {
        tokens = new TokenList
        errors = 0

        for (file <- files) {
            currentFile = file
            if (!parseFile(directory, file)) {
                errors += 1
            }
            System.gc()
        }

        tokens
    }

    private def parseFile(directory: File, fileName: String): Boolean = {
        currentFile = fileName

        try {
            val file = new File(directory, fileName)
            val bytes = java.nio.file.Files.readAllBytes(file.toPath)
            val text = new String(bytes, "UTF-8")
            val input = Input.VirtualFile(file.getPath, text)
            val ast = input.parse[Source].get
            traverser(ast)

            add(FileEnd, text.count(_ == '\n') - 1, 0, 0)
        } catch {
            case exception: Throwable =>
                exception.printStackTrace()
                return false
        }

        true
    }

    /**
     * Adds a token to the token list.
     *
     * @param tokenType the type of the token
     * @param line      line of the occurrence in the file
     * @param column    column of the occurrence in the file
     * @param length    length of the occurrence in the file
     */
    private def add(tokenType: ScalaTokenConstants.Value, line: Int, column: Int, length: Int): Unit = {
        tokens.addToken(new ScalaToken(tokenType.id, currentFile, line, column, length))
    }


    /**
     * Adds a token to the token list.
     *
     * @param tokenType the type of the token
     * @param node      the tree that marks the occurrence
     * @param fromEnd   if true, the token is added at the end of the tree (length 0).
     */
    private def add(tokenType: ScalaTokenConstants.Value, node: Tree, fromEnd: Boolean): Unit = {
        if (node.pos.text.nonEmpty) {
            // SELF type tokens with no text content mess up the sequence
            tokens.addToken(new ScalaToken(tokenType, currentFile, node.pos, fromEnd))
        }
    }

}