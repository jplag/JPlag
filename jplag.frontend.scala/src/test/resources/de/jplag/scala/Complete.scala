package mypackage

import scala.annotation.tailrec
import scala.otherpackage

class Complete extends SuperClass with MyTrait {

    val constantMember = 1

    def method(arg: ArgType): Unit = {

        var variable : Int = 2

        do method() while (false)
        while (false) method()

        for (index <- 0 to 10 by 1 if isPrime(index)) {
            method(index)
            (index % 2) match {
                case 0 => "even"
                case 1 => "odd"
            }
        }

        if (true) {

        } else {

        }

        try {
            method()
            throw new Exception()
        } catch {
            case exception : Exception if true =>
            case _ =>
        } finally {

        }

        val anonymousClassObject = new AnyVal {}

    }

    def getOne() : Int = {
        return 1
    }

    object NumberGenerator {

        def getNumber(i : Int): Unit = {
            if (i <= 0) i else 1 + getNumber(i - 1)
        }

    }

    class Person(var firstName: String, var lastName: String) {

        def this(var firstName: String) = {
            this(firstName, "Smith")
        }
    }

    def create(): Unit = {
        val obj = new Person("Mickey", "Mouse")
    }

    def applyPartialFunction(strings: Seq[String]) =
        strings.collect({
            case s if s.length > 5 => s
            case _ =>
        })

    def applyTotalFunction(strings: Seq[String]) =
        strings.find(s => {
            s.substring(0,3).equals("abc")
        })

    def callMacro(msg: Any) = macro macroImpl

    def macroImpl(c: Context)(msg: c.Expr[Any]) = println(msg)

    def yieldingMethod(list: Seq[String]) = {
        for (string <- list if string.startsWith("_")) yield string.substring(1)
    }

    trait MyTrait[U] {
        self =>
        type T <: U
        val member: T
    }

    class MyTraitImpl extends MyTrait[Complete] {}

    def reverse[T](list: Seq[T]): Seq[T] = {
        if (list.isEmpty) list
        else reverse(list.tail) :: list.head
    }

    def useReverse(): Unit = {
        val list = Array[Int](3, 2, 1)
        reverse[Int](list)
    }

    def innerBlock(): Unit = {
        val a = 1
        {
            val b = 2
        }
        println("b is not accessible here.")
    }

    def operators(): Unit = {
        // This is not treated as a function call
        val a = 1 + 2
        // This is treated as a function call
        val b = (1).+(2)
    }

    def functionObject(): Unit = {
        var f : Int => Int = _ * 2
        f(2)

        ((i: Int) => i * 2)(2)
    }

    def memberChain(): Unit = {
        a.b.c
    }

    def infix() : Unit = {
        val list = List(1,2,3)
        val list2 = list reverse_::: list
    }

    def power(base: Int, exponent: Int): Int = {
        if (exponent == 0) 1
        else if (exponent == 1) base
        else if (exponent % 2 == 0) {
            ((i: Int) => i * i) (power(base, exponent / 2))
        }
        else base * power(base, exponent - 1)
    }

    def methodCall() : Unit = {
        myObject.member // may be member reference or method call
        // gets MEMBER token

        myObject.member2() // must be method call
        // gets MEMBER token

        myObject.member3(arg1, arg2) // must be method call
        // gets APPLY MEMBER ARG ARG tokens
    }

    /*
    Enums are a feature of Scala3, not yet supported by the parser.
    enum Menu:
        case PizzaMargharita, SpaghettiBolognese, Fries
    */

}
