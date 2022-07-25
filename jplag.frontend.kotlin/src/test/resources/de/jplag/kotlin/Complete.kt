package de.jplag.kotlin

import kotlin.io.println

/**
 * A file that should produce a complete set of [KotlinToken]s when parsed by the JPlag Kotlin frontend.
 * Other than that, there is no inherent meaning to the code, and it is obviously full of redundancies and less than perfect stylistic choices.
 */
class Complete() {

    object Inner {
        var accessCount: Int = 0
            get() = field
            set(value) {
                field = value
            }

        private var objectNumber: Int

        init {
            this.objectNumber = ++counter.count
        }
    }
    companion object counter {
        var count = 0;
    }

    fun get() : Inner {
        Inner.accessCount = Inner.accessCount + 1;
        return Inner
    }

    fun setCounter(value : Int) : Unit {
        count = value
    }

    fun getAccessCount() : Int = Inner.accessCount

}

class Container<T>(value : T) : Any() {

    private val value: T
    init {
        this.value = value;
    }

    fun isTheAnswer(): Boolean {
        return this.value == 42
    }
}

enum class Enumeration {
    VALUE1, VALUE2, VALUE3;
}

fun main(args : Array<String>) {
    val complete = Complete();
    for (idx in 1..3) {
        complete.get()
    }
    println(complete.getAccessCount())

    var int : Int = 0;
    loop@ while (true) {
        val box = Container(int);
        if (box.isTheAnswer()) {
            println("Finally!")
            break@loop
        }
        int = int + 2;
    }

    try {
        do {
            println(1/0)
            continue
        } while (false)
    } catch (exception : ArithmeticException) {
        println("This should not have happened.")
    } finally {
        println("Now, let's carry on.")
    }

    val weekday = "Friday"
    val message = when (weekday) {
        "Monday", "Tuesday" -> "Oh no :c"
        "Saturday", "Sunday" -> "It's the weekend, yay :)"
        else -> {
            if (!Regex("[A-Z][a-z]*").matches(weekday)) {
                throw IllegalArgumentException("$weekday is not a valid day of the week!")
            } else "It's almost weekend. Stay strong :)"
        }
    }

}

