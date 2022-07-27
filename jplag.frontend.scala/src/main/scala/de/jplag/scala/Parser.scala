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
            c =>
                add(ScalaTokenConstants.CaseBegin, c, fromEnd=false)
                apply(c)
                add(ScalaTokenConstants.CaseEnd, c, fromEnd=true)
        }

        def isNotArithmetic(op: Term.Name) : Boolean = {
            op.value.matches("""[_\w]+""")
        }

        private def doMatch(tree: Tree): TR = {
            tree match {
                case Term.Do(_) => TR(Some(DoBegin), Some(DoEnd))
                case Term.Assign(_) => TR(Some(Assign), None)
                case Term.While(_) => TR(Some(WhileBegin), Some(WhileEnd))
                case Term.For(_) =>
                    TR(Some(ForBegin), Some(ForEnd))
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
                        add(CatchBegin, catchp, fromEnd=false)
                        apply(catchp)
                        add(CatchEnd, catchp, fromEnd = true)

                        maybeAddAndApply(finallyp, ScalaTokenConstants.Finally)
                    })
                case Term.Apply(fun, args) =>
                    // `f()` can also be written as `f`, so we simply don't see `f()` as a function call
                    // But Java style function calls with no args should still be recognized
                    TR(traverse = _ => {
                        add(Apply, tree, fromEnd=false)
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
                case Term.NewAnonymous(_) => TR(Some(NewCreationBegin), Some(ScalaTokenConstants.NewCreationBegin))
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
                    add(ForBegin, tree, fromEnd=false)
                    add(Yield, tree, fromEnd = false)

                    tree.children.foreach(apply)

                    add(ForEnd, tree, fromEnd = true)
                })
                case Term.If(conditionExpr, thenExpression, elseExpression) => TR(traverse = _ => {
                    add(If, tree, fromEnd=false)
                    apply(conditionExpr)
                    add(IfBegin, thenExpression, fromEnd=false)

                    apply(thenExpression)
                    add(IfEnd, thenExpression, fromEnd=true)

                    elseExpression match {
                        case Lit.Unit() => apply(elseExpression)
                        case _ =>
                            val elseStart = tree.pos.text.indexOf("else", thenExpression.pos.end - tree.pos.start)
                            val elsePosition = Position.Range(tree.pos.input, tree.pos.start + elseStart, tree.pos.start + elseStart + 4)
                            add(Else, elsePosition.startLine + 1, elsePosition.startColumn + 1, elsePosition.text.length)
                            add(ElseBegin, elseExpression, fromEnd = false)
                            apply(elseExpression)
                            add(ElseEnd, elseExpression, fromEnd = true)
                    }
                })

                case scala.meta.Pkg(_) => TR(Some(Package))
                case scala.meta.Import(_) => TR(Some(ScalaTokenConstants.Import))

                case Defn.Def(_) => TR(Some(MethodBegin), Some(MethodEnd))
                case Defn.Macro(_) => TR(Some(MacroBegin), Some(MacroEnd))
                case Defn.Class(_) => TR(Some(ClassBegin), Some(ClassEnd))
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
                case Decl.Var(_) => TR(Some(VariableDefinition))
                case Decl.Def(_) => TR(Some(MethodBegin), Some(MethodEnd))
                case Decl.Type(_) => TR(Some(ScalaTokenConstants.Type))

                case Ctor.Secondary(_) => TR(Some(ConstructorBegin), Some(ConstructorEnd))

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
                case Term.ApplyType(fun, typeArgs) => TR(traverse = _ => {
                    add(Apply, tree, fromEnd = false)
                    for (typeArg <- typeArgs) add(TypeArgument, typeArg, fromEnd = false)
                })
                case Term.New(_) => TR(Some(NewObject))
                case Self(_) => TR(Some(SelfType))
                case Term.Block(_) => TR(Some(BlockStart), Some(BlockEnd))
                case Enumerator.Generator(_) => TR(Some(EnumGenerator))
                case _ => TR()
            }
        }

        override def apply(tree: Tree): Unit = {
            val res = doMatch(tree)

            res.before match {
                case Some(value) => add(value, tree, fromEnd = false)
                case None =>
            }

            res.traverse(tree)

            res.after match {
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