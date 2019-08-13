package jplag.scala

import java.io.File

import jplag.Structure

import jplag.scala.ScalaTokens._
import scala.meta._


object Parser extends jplag.Parser {
  private var struct = new Structure
  private var currentFile: String = _

  def main(args: Array[String]): Unit = {
    val parser = Parser
    val struct: jplag.Structure = parser.parse(null, Array("/home/thomas/test.scala"))
    for (token <- struct.tokens) {
      if (token == null) {
        return
      }
      println(token.getLine, ScalaTokens(token.`type`).toString)
    }
  }


  private val traverser: Traverser = new Traverser {

    case class TR(before: Option[ScalaTokens.Value] = None,
                              after: Option[ScalaTokens.Value] = None,
                              traverse: Tree => Unit = _.children.foreach(traverser.apply))

    private def maybeAddAndApply(tree: Option[Tree], token: ScalaTokens.Value): Unit = tree match {
      case Some(exp) =>
        add(token, exp, fromEnd = false)
        apply(exp)
      case None =>
    }

    private def processCases(cases: List[Case]) : Unit = cases.foreach {
      c =>
        add(ScalaTokens.Case, c, fromEnd=false)
        apply(c)
    }

    private def doMatch(tree: Tree): TR = {
      tree match {
        case Term.Do(_) => TR(Some(DoBegin), Some(DoEnd))
        case Term.Assign(_) => TR(Some(Assign), None)
        case Term.While(_) => TR(Some(WhileBegin), Some(WhileEnd))
        case Term.For(_) => TR(Some(ForBegin), Some(ForEnd))
        case Term.Try(expr, catchp, finallyp) => TR(Some(TryBegin),
          traverse = _ => {
            apply(expr)
            if (catchp.nonEmpty) {
              val start = catchp.head.pos
              val end = catchp.last.pos
              val len = end.endLine - start.startLine

              add(CatchBegin, start.startLine, start.startColumn, len)
              processCases(catchp)
              add(CatchEnd, end.endLine, end.endColumn, len)
            }

            maybeAddAndApply(finallyp, ScalaTokens.Finally)
          })
        case Term.TryWithHandler(expr, catchp, finallyp) => TR(Some(TryBegin),
          traverse = _ => {
            apply(expr)
            add(CatchBegin, catchp, fromEnd=false)
            apply(catchp)
            add(CatchEnd, catchp, fromEnd = true)

            maybeAddAndApply(finallyp, ScalaTokens.Finally)
          })
        case Term.Apply(fun, args) =>
          // `f()` can also be written as `f`, so we simply don't see `f()` as a function call
          TR(traverse = _ => {
            if (args.nonEmpty) {
              add(Apply, tree, fromEnd=false)
            }
            apply(fun)
            for (arg <- args) {
              arg match {
                case Term.Assign(lhs, rhs) =>
                  apply(lhs)
                  apply(rhs)
                case _ => apply(arg)
              }
            }
          })
        case Term.NewAnonymous(_) => TR(Some(NewCreationBegin), Some(ScalaTokens.NewCreationBegin))
        case Term.Return(_) => TR(Some(ScalaTokens.Return))
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
        case Term.If(cond, thenp, elsep) => TR(traverse = _ => {
          add(IfBegin, tree, fromEnd=false)
          apply(cond)

          apply(thenp)

          elsep match {
            case Lit.Unit() =>
            case _ =>
              add(ScalaTokens.Else, elsep, fromEnd=false)
          }
          apply(elsep)

          add(IfEnd, tree, fromEnd=true)
        })

        case scala.meta.Pkg(_) => TR(Some(Package))
        case scala.meta.Import(_) => TR(Some(ScalaTokens.Import))

        case Defn.Def(_) => TR(Some(MethodBegin), Some(MethodEnd))
        case Defn.Macro(_) => TR(Some(MacroBegin), Some(MacroEnd))
        case Defn.Class(_) => TR(Some(ClassBegin), Some(ClassEnd))
        case Defn.Object(_) => TR(Some(ObjectBegin), Some(ObjectEnd))
        case Defn.Trait(_) => TR(Some(TraitBegin), Some(TraitEnd))
        case Defn.Type(_) => TR(Some(ScalaTokens.Type))
        case Defn.Var(mods, pats, decltype, rhs) => TR(traverse = _ => {
          apply(mods)
          for (pat <- pats) {
            add(Vardef, pat, fromEnd=false)
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
            add(Vardef, pat, fromEnd=false)
            apply(pat)
            apply(decltype)

            add(Assign, rhs, fromEnd=false)
            apply(rhs)
          }

        })

        case Decl.Var(_) => TR(Some(Vardef))
        case Decl.Var(_) => TR(Some(Vardef))
        case Decl.Def(_) => TR(Some(MethodBegin), Some(MethodEnd))
        case Decl.Type(_) => TR(Some(ScalaTokens.Type))

        case Ctor.Secondary(_) => TR(Some(ConstructorBegin), Some(ConstructorEnd))

        case Enumerator.Guard(_) => TR(Some(Guard))

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

  def parse(dir: File, files: Array[String]): jplag.Structure = {
    struct = new Structure
    errors = 0

    for (file <- files) {
      currentFile = file
      // getProgram.print(null, "Parsing file" + file + "\n")
      if (!parseFile(dir, file)) {
        errors += 1
      }
      System.gc()
    }

    this.parseEnd()
    struct
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

      add(FileEnd, text.count(_ == '\n') + 1, 0, 0)
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        return false
    }

    true
  }

  private def add(typ: ScalaTokens.Value, line: Int, column: Int, length: Int): Unit = {
    struct.addToken(new ScalaToken(typ.id, currentFile, line, column, length))
  }


  private def add(typ: ScalaTokens.Value, node: Tree, fromEnd: Boolean): Unit = {
    struct.addToken(new ScalaToken(node, fromEnd, currentFile, typ))
  }
}