package mypackage

import scala.otherpackage

trait MyTrait extends SuperTrait with OtherTrait {
  self: SelfType =>

  final val valAssignee = assigned

  private val rightAssocValDefs = new mutable.AnyRefMap[Symbol, Tree]

  sealed abstract class MyClass[+T] {
    def attribute: Boolean

    @inline final def methodWithTypeParam[U](none: => U)(f: T => U): U = this match {
      case SubType(attr) => f(attr)
      case _                        => none
    }

  }

  object MyObject extends SuperType {

    override def apply(t: Type): Type = {
      def checkNoEscape(sym: Symbol): Unit = {
        while (o != NoSymbol && o != sym.owner && o != sym.owner.linkedClassOfClass &&
          !o.isLocalToBlock && !o.isPrivate &&
          !o.privateWithin.hasTransOwner(sym.owner))
          o = o.owner
      }

    }
  }

  private final val typerFreshNameCreators = perRunCaches.newAnyRefMap[Symbol, FreshNameCreator]()

  abstract class MyClass2(constrArg: ParamType) extends SuperClass with Trait1 with Trait2 {

    val anonymousSubclassObject = new scala.AnyVal {   }

    def applyImplicitArgs(fun: Tree): Tree = fun.tpe match {
      case MethodType(params, _) =>
        var mkArg: (Name, Tree) => Tree = (_, tree) => tree

        for(param <- params) {
          var paramTp = param.tpe
        }

      case x => throw new MatchError(x)
    }

    def tryCatchFinally() = try {
      if (true) false
      else otherMethod(arg)
    } catch {
      case ex: CyclicReference => throw ex
      case _ => return true
    } finally {
      cleanUp()
    }

    def yieldingMethodWithGuard(values: List[Int]): List[Int] =
      for (value <- values if value > 0) yield {
        value
      }

    def validateParentClasses(parents: List[Tree], selfType: Type, clazzIsTrait: Boolean): Unit = {
      (parent.tpe :: ps).collectFirst {
        case p if hasTraitParams(p.typeSymbol) =>
          p.typeSymbol.attachments.get[DottyParameterisedTrait].foreach(attach =>
            pending += ParentIsScala3TraitError(parent, p.typeSymbol, attach.params, psym)
          )
      }
    }


  }

}
