/**
 * A number-guessing game implemented in Kotlin.
 */
fun main() {
    val upperLimit = 100

    var candidates = (1..upperLimit).toList()

    var filters = (1..upperLimit).map({ it -> listOf(greaterThanFilter(it), lessThanFilter(it), multipleOfFilter(it))}).flatten()
    while (candidates.size > 1) {
        filters = filters.sortedWith(FilterComparator(candidates))
        val chosen = filters.get(0)
        println("Is your number " + chosen + "?" )
        val answer = readBoolean()
    }

}

class Filter(desc: String, f: (Int) -> Boolean) {
    val f = f;
    val desc = desc;

    fun apply(ns: List<Int>) = ns.filter(this.f)
    fun rate(ns: List<Int>) = Math.abs(apply(ns).size - opposite().apply(ns).size)
    fun opposite() = Filter("not " + this.desc, { m: Int -> !this.f(m) })
    override fun toString() = this.desc;
}

class FilterComparator(ns: List<Int>) : Comparator<Filter> {
    val ns = ns;

    override fun compare(p0: Filter, p1: Filter) : Int = p0.rate(ns) - p1.rate(ns)
}

fun greaterThanFilter(n: Int) : Filter = Filter("greater than " + n, { m: Int -> m > n })
fun lessThanFilter(n: Int) : Filter = Filter("less than " + n, { m: Int -> m < n })

fun multipleOfFilter(n: Int) : Filter = Filter("a multiple of "  + n, { m: Int -> m % n == 0 })

fun readBoolean() : Boolean {
    var input = readLine()!!.toString();
    while (!Regex("yYnN").matches(input)) {
        println("Hmm, try again.")
        input = readLine().toString();
    }
    return Regex("yY").matches(input)
}
