package datastructures

sealed class List<out A> {

    companion object {
        fun <A> of(vararg aa: A): List<A> {
            val tail = aa.sliceArray(1 until aa.size)
            return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
        }

        fun <A> empty(): List<A> = Nil
    }

    fun <A> List<A>.tail(): List<A> {
        return when (this) {
            is Nil -> throw IllegalStateException("Nil cannot have tail")
            is Cons -> this.tail
        }
    }

    fun <A> List<A>.setHead(x: A): List<A> {
        return when (this) {
            is Cons -> Cons(x, this.tail.setHead(this.head))
            is Nil -> Cons(x, Nil)
        }
    }

    fun <A> List<A>.drop(n: Int): List<A> {
        return if (n == 0) this
        else when (this) {
            is Cons -> this.tail.drop(n - 1)
            Nil -> throw Exception("List is too short")
        }
    }

    fun <A> List<A>.dropWhile(f: (A) -> Boolean): List<A> {
        return when (this) {
            is Cons -> {
                if (f(this.head)) this.tail.dropWhile(f)
                else this
            }
            Nil -> Nil
        }
    }
}

object Nil : List<Nothing>()
data class Cons<out A>(val head: A, val tail: List<A>) : List<A>()

fun sum(numbers: List<Int>): Int {
    return when (numbers) {
        is Nil -> 0
        is Cons -> numbers.head + sum(numbers.tail)
    }
}

fun <A, B> foldRight(xs: List<A>, z: B, f: (A, B) -> B): B {
    return when (xs) {
        is Nil -> z
        is Cons -> f(xs.head, foldRight(xs.tail, z, f))
    }
}

tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B {
    return when (xs) {
        is Nil -> z
        is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
    }
}

fun sumFold(ints: List<Int>): Int =
    foldRight(ints, 0) { a, b -> a + b }

fun sumLeftFold(ints: List<Int>): Int =
    foldLeft(ints, 0) { a, b -> a + b }

fun productFold(numbers: List<Double>) =
    foldRight(numbers, 1.0) { a, b -> a * b }

fun productLeftFold(numbers: List<Double>) =
    foldLeft(numbers, 1.0) { a, b -> a * b }

fun <A> length(xs: List<A>): Int {
    return foldRight(xs, 0) { _, b -> 1 + b }
}

fun <A> lengthLeftFold(xs: List<A>): Int {
    return foldLeft(xs, 0) { a, _ -> 1 + a }
}

fun <A> reverse(xs: List<A>): List<A> {
    return foldLeft(xs, List.empty()) { tail, head -> Cons(head, tail) }
}

fun <A> append(a1: List<A>, a2: List<A>): List<A> {
    return foldRight(a1, a2) { h, t -> Cons(h, t) }
}

fun <A> concatenate(list: List<List<A>>): List<A> {
    return foldRight(list, List.empty()) { a, b -> append(a, b) }
}

fun addOne(list: List<Int>): List<Int> =
    foldRight(list, List.empty()) { h, t -> Cons(h + 1, t) }

fun doubleToString(numbers: List<Double>): List<String> =
    foldRight(numbers, List.empty()) { h, t -> Cons(h.toString(), t) }

fun <A, B> map(list: List<A>, f: (A) -> B): List<B> =
    foldRight(list, List.empty()) { h, t -> Cons(f(h), t) }

fun <A> filter(list: List<A>, f: (A) -> Boolean): List<A> =
    foldRight(list, List.empty()) { h, t -> if (f(h)) Cons(h, t) else t }

fun <A, B> flatMap(list: List<A>, f: (A) -> List<B>): List<B> =
    concatenate(map(list, f))

// Solution from book
fun <A, B> flatMap2(xa: List<A>, f: (A) -> List<B>): List<B> =
    foldRight(
        xa,
        List.empty()
    ) { a, lb -> append(f(a), lb) }

fun <A> filterWithFlatMap(list: List<A>, f: (A) -> Boolean): List<A> =
    flatMap(list) { if (f(it)) List.of(it) else List.empty() }

fun zipWithSum(list1: List<Int>, list2: List<Int>): List<Int> =
    when (list1) {
        is Nil -> Nil
        is Cons -> when (list2) {
            is Nil -> Nil
            is Cons -> Cons(list1.head + list2.head, zipWithSum(list1.tail, list2.tail))
        }
    }

fun <A, B> zipWith(list1: List<A>, list2: List<A>, f: (A, A) -> B): List<B> =
    when (list1) {
        is Nil -> Nil
        is Cons -> when (list2) {
            is Nil -> Nil
            is Cons -> Cons(f(list1.head, list2.head), zipWith(list1.tail, list2.tail, f))
        }
    }

