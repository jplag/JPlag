package de.jplag.scala

import de.jplag.scala.ScalaTokenConstants._
import de.jplag.{AbstractParser, ErrorConsumer, TokenList}

import java.io.File
import scala.meta._


class Parser(consumer: ErrorConsumer) extends AbstractParser(consumer) {
    private var currentFile : String = _

    var tokens : TokenList = _

    private var lastInMethod: List[Boolean] = List[Boolean]()

    private val traverser: Traverser = new Traverser {

        case class TR(before: Option[ScalaTokenConstants.Value] = None,
                      after: Option[ScalaTokenConstants.Value] = None,
                      traverse: Tree => Unit = _.children.foreach(traverser.apply)
                     )

        private def maybeAddAndApply(tree: Option[Tree], token: ScalaTokenConstants.Value): Unit = tree match {
            case Some(exp) =>
                add(token, exp, fromEnd = false)
                apply(exp)
            case None =>
        }

        private def processCases(cases: List[Case]) : Unit = cases.foreach {
            case c@Case(pattern, condition, body) =>
                add(ScalaTokenConstants.CaseStatement, c, fromEnd = false)
                apply(pattern)
                apply(condition)
                enclose(body, TR(Some(CaseBegin), Some(CaseEnd)))
            case _ =>
        }

        def isNotArithmetic(op: Term.Name) : Boolean = {
            op.value.matches("""[_\w]+""")
        }

        private def doMatch(tree: Tree): TR = {
            tree match {
                case Term.Do(body, condition) => TR(Some(DoWhile), Some(DoWhileEnd), traverse = _ => {
                    enclose(body, TR(Some(DoBodyBegin), Some(DoBodyEnd)))
                    apply(condition)
                })
                case Term.Assign(_) => TR(Some(Assign), None)
                case Term.While(condition, body) => TR(Some(While), traverse = _ => {
                    apply(condition)
                    enclose(body, TR(Some(WhileBodyBegin), Some(WhileBodyEnd)))
                })
                case Term.For(enumerators, body) =>
                    TR(Some(For), traverse = _ => {
                        apply(enumerators)
                        enclose(body, TR(Some(ForBodyBegin), Some(ForBodyEnd)))
                    })
                case Term.Try(expr, catchExpr, finallyExpr) => TR(Some(TryBegin),
                    traverse = _ => {
                        apply(expr)
                        if (catchExpr.nonEmpty) {
                            val start = catchExpr.head.pos
                            val end = catchExpr.last.pos
                            val len = end.endLine - start.startLine

                            add(CatchBegin, start.startLine, start.startColumn, len)
                            processCases(catchExpr)
                            add(CatchEnd, end.endLine, end.endColumn, len)
                        }

                        maybeAddAndApply(finallyExpr, ScalaTokenConstants.Finally)
                    })
                case Term.TryWithHandler(expr, catchp, finallyp) => TR(Some(TryBegin),
                    traverse = _ => {
                        apply(expr)
                        enclose(catchp, TR(Some(CatchBegin), Some(CatchEnd)))

                        maybeAddAndApply(finallyp, ScalaTokenConstants.Finally)
                    })
                case Term.Apply(fun, args) =>
                    // `f()` can also be written as `f`, so we simply don't see `f()` as a function call
                    // But Java style function calls with no args should still be recognized
                    TR(traverse = _ => {
                        add(Apply, fun, fromEnd=false)
                        apply(fun)
                        for (arg <- args) {
                            add(Argument, arg, fromEnd = false)
                            arg match {
                                case Term.Assign(lhs, rhs) =>
                                    apply(lhs)
                                    apply(rhs)
                                case _ => apply(arg)
                            }
                        }
                    })
                case Term.NewAnonymous(_) => TR(Some(NewCreationBegin), Some(NewCreationEnd))
                case Term.Return(_) => TR(Some(ScalaTokenConstants.Return))
                case Term.Match(expr, cases) => TR(traverse = _ => {
                    add(MatchBegin, tree, fromEnd=false)
                    apply(expr)
                    processCases(cases)
                    add(MatchEnd, tree, fromEnd=true)
                })
                case Term.Throw(_) => TR(Some(Throw))
                case Term.Function(_) => TR(Some(FunctionBegin), Some(FunctionEnd))
                case Term.PartialFunction(cases) => TR(traverse = _ => {
                    add(PartialFunctionBegin, tree, fromEnd=false)
                    processCases(cases)
                    add(PartialFunctionEnd, tree, fromEnd=true)
                })
                case Term.ForYield(_) => TR(traverse = _ => {
                    add(ForBodyBegin, tree, fromEnd=false)
                    add(Yield, tree, fromEnd = false)

                    tree.children.foreach(apply)

                    add(ForBodyEnd, tree, fromEnd = true)
                })
                case Term.If(conditionExpr, thenExpression, elseExpression) => TR(traverse = _ => {
                    add(If, tree, fromEnd=false)
                    apply(conditionExpr)

                    enclose(thenExpression, TR(Some(IfBegin), Some(IfEnd)))

                    elseExpression match {
                        case Lit.Unit() => apply(elseExpression)
                        case _ =>
                            val elseStart = tree.pos.text.indexOf("else", thenExpression.pos.end - tree.pos.start)
                            val elsePosition = Position.Range(tree.pos.input, tree.pos.start + elseStart, tree.pos.start + elseStart + 4)
                            add(Else, elsePosition.startLine + 1, elsePosition.startColumn + 1, elsePosition.text.length)
                            enclose(elseExpression, TR(Some(ElseBegin), Some(ElseEnd)))
                    }
                })

                case scala.meta.Pkg(_) => TR(Some(Package))
                case scala.meta.Import(_) => TR(Some(ScalaTokenConstants.Import))

                case Defn.Def(mods, name, typeParams, paramss, _, body) =>
                    TR(traverse = _ => {
                        applyRecursively(mods)
                        add(MethodDef, name, fromEnd = false)
                        for (tParam <- typeParams) add(TypeParameter, tParam, fromEnd = false)
                        for (params <- paramss)
                            for (param <- params) add(Parameter, param, fromEnd = false)

                        enclose(body, TR(Some(MethodBegin), Some(MethodEnd)))
                    })
                case Defn.Macro(mods, name, tparams, paramss, declType, body) => TR(Some(Macro), traverse = _ => {
                    applyRecursively(mods)
                    applyRecursively(tparams)
                    applyRecursively(paramss)
                    enclose(body, TR(Some(MacroBegin), Some(MacroEnd)))
                })
                case Defn.Class(_) =>
                    TR(Some(ClassBegin), Some(ClassEnd))
                case Defn.Object(_) => TR(Some(ObjectBegin), Some(ObjectEnd))
                case Defn.Trait(_) => TR(Some(TraitBegin), Some(TraitEnd))
                case Defn.Type(_) => TR(Some(ScalaTokenConstants.Type))
                case Defn.Var(mods, pats, decltype, rhs) => TR(traverse = _ => {
                    apply(mods)
                    for (pat <- pats) {
                        add(VariableDefinition, pat, fromEnd=false)
                        apply(pat)
                        apply(decltype)

                        rhs match {
                            case Some(realRhs) =>
                                add(Assign, realRhs, fromEnd=false)
                                apply(realRhs)
                            case None =>
                        }
                    }
                })
                case Defn.Val(mods, pats, decltype, rhs) => TR(traverse = _ => {
                    apply(mods)
                    for (pat <- pats) {
                        add(VariableDefinition, pat, fromEnd=false)
                        apply(pat)
                        apply(decltype)

                        add(Assign, rhs, fromEnd=false)
                        apply(rhs)
                    }

                })

                case Decl.Var(_) => TR(Some(VariableDefinition))
                case Decl.Val(_) => TR(Some(VariableDefinition))
                case Decl.Def(_) => TR(Some(MethodBegin), Some(MethodEnd))
                case Decl.Type(_) => TR(Some(ScalaTokenConstants.Type))

                case Ctor.Secondary(_) =>
                    TR(Some(ConstructorBegin), Some(ConstructorEnd))
                case Init(tpe, name, argss) if argss.nonEmpty => TR(traverse = _ => {
                    for (args <- argss) {
                        for (arg <- args) {
                            add(Argument, arg, fromEnd = false)
                            apply(arg)
                        }
                    }
                })
                case Enumerator.Guard(_) => TR(Some(Guard))

                case Term.Param(_) => TR(traverse = _ => add(Parameter, tree, fromEnd = false))
                case Term.NewAnonymous(_) => TR(Some(ClassBegin), Some(ClassEnd))
                case Term.ApplyInfix(_, op, _, _) if op.value.contains("=") && !Array("==", "!=").contains(op.value) => TR(Some(Assign))
                case Term.ApplyInfix(fun, op, typeArgs, args) if isNotArithmetic(op) => TR(traverse = _ => {
                    add(Apply, tree, fromEnd = false)
                    apply(fun)
                    for (typeArg <- typeArgs) {
                        add(TypeArgument, typeArg, fromEnd = false)
                        apply(typeArg)
                    }
                    for (arg <- args) {
                        add(Argument, arg, fromEnd = false)
                        apply(arg)
                    }
                })
                case Term.Select(refObj, member) =>
                    TR(traverse = _ => {
                        apply(refObj)
                        add(ScalaTokenConstants.Member, member, fromEnd = false)
                        apply(member)
                    })
                case Term.ApplyType(_, typeArgs) => TR(traverse = _ => {
                    add(Apply, tree, fromEnd = false)
                    for (typeArg <- typeArgs) add(TypeArgument, typeArg, fromEnd = false)
                })
                case Term.New(_) => TR(Some(NewObject))
                case Self(_) => TR(Some(SelfType))
                case block@Term.Block(_) if block.parent.nonEmpty && block.parent.get.isInstanceOf[Term.Apply] =>
                    TR(Some(BlockStart), Some(BlockEnd))
                case Enumerator.Generator(_) => TR(Some(EnumGenerator))
                case meta.Type.Param(_) => TR(Some(TypeParameter))

                case _ => TR()
            }
        }

        def applyRecursively[T](els: List[T]): Unit = els.foreach( {
             case tree: Tree => apply(tree)
             case treeList: List[_] => applyRecursively(treeList)
        })

        override def apply(tree: Tree): Unit = {
            val record : TR = doMatch(tree)

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

        def enclose(tree: Tree, record: TR): Unit = {
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



    def parse(dir: File, files: Array[String]): TokenList = {
        tokens = new TokenList
        errors = 0

        for (file <- files) {
            currentFile = file
            // getProgram.print(null, "Parsing file" + file + "\n")
            if (!parseFile(dir, file)) {
                errors += 1
            }
            System.gc()
        }

        tokens
    }

    def parseFile(dir: File, file: String): Boolean = {
        currentFile = file

        try {
            val f = new File(dir, file)
            val bytes = java.nio.file.Files.readAllBytes(f.toPath)
            val text = new String(bytes, "UTF-8")
            val input = Input.VirtualFile(f.getPath, text)
            val ast = input.parse[Source].get
            traverser(ast)

            add(FileEnd, text.count(_ == '\n')-1, 0, 0)
        } catch {
            case e: Throwable =>
                e.printStackTrace()
                return false
        }

        true
    }

    private def add(tType: ScalaTokenConstants.Value, line: Int, column: Int, length: Int): Unit = {
        tokens.addToken(new ScalaToken(tType.id, currentFile, line, column, length))
    }



    private def add(tType: ScalaTokenConstants.Value, node: Tree, fromEnd: Boolean): Unit = {
        if (node.pos.text.nonEmpty) {
            // SELF type tokens with no text content mess up the sequence
            tokens.addToken(new ScalaToken(tType, currentFile, node.pos, fromEnd))
        }
    }

}