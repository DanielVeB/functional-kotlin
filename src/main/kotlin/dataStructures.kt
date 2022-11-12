sealed class FunctionalList<out A> {

    companion object {
        fun <A> of(vararg aa: A): FunctionalList<A> {
            val tail = aa.sliceArray(1 until aa.size)
            return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
        }

        fun <A> empty(): FunctionalList<A> = Nil
    }

    fun <A> FunctionalList<A>.tail(): FunctionalList<A> {
        return when (this) {
            is Nil -> throw IllegalStateException("Nil cannot have tail")
            is Cons -> this.tail
        }
    }

    fun <A> FunctionalList<A>.setHead(x: A): FunctionalList<A> {
        return when (this) {
            is Cons -> Cons(x, this.tail.setHead(this.head))
            is Nil -> Cons(x, Nil)
        }
    }

    fun <A> FunctionalList<A>.drop(n: Int): FunctionalList<A> {
        return if (n == 0) this
        else when (this) {
            is Cons -> this.tail.drop(n - 1)
            Nil -> throw Exception("List is too short")
        }
    }

    fun <A> FunctionalList<A>.dropWhile(f: (A) -> Boolean): FunctionalList<A> {
        return when (this) {
            is Cons -> {
                if (f(this.head)) this.tail.dropWhile(f)
                else this
            }

            Nil -> Nil
        }
    }
}

object Nil : FunctionalList<Nothing>()
data class Cons<out A>(val head: A, val tail: FunctionalList<A>) : FunctionalList<A>()

fun sum(numbers: FunctionalList<Int>): Int {
    return when (numbers) {
        is Nil -> 0
        is Cons -> numbers.head + sum(numbers.tail)
    }
}

fun <A, B> foldRight(xs: FunctionalList<A>, z: B, f: (A, B) -> B): B {
    return when (xs) {
        is Nil -> z
        is Cons -> f(xs.head, foldRight(xs.tail, z, f))
    }
}

tailrec fun <A, B> foldLeft(xs: FunctionalList<A>, z: B, f: (B, A) -> B): B {
    return when (xs) {
        is Nil -> z
        is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
    }
}

fun sumFold(ints: FunctionalList<Int>): Int =
    foldRight(ints, 0) { a, b -> a + b }

fun sumLeftFold(ints: FunctionalList<Int>): Int =
    foldLeft(ints, 0) { a, b -> a + b }

fun productFold(numbers: FunctionalList<Double>) =
    foldRight(numbers, 1.0) { a, b -> a * b }

fun productLeftFold(numbers: FunctionalList<Double>) =
    foldLeft(numbers, 1.0) { a, b -> a * b }

fun <A> length(xs: FunctionalList<A>): Int {
    return foldRight(xs, 0) { _, b -> 1 + b }
}

fun <A> lengthLeftFold(xs: FunctionalList<A>): Int {
    return foldLeft(xs, 0) { a, _ -> 1 + a }
}

fun <A> reverse(xs: FunctionalList<A>): FunctionalList<A> {
    return foldLeft(xs, FunctionalList.empty()) { tail, head -> Cons(head, tail) }
}

fun <A> append(a1: FunctionalList<A>, a2: FunctionalList<A>): FunctionalList<A> {
    return foldRight(a1, a2) { h, t -> Cons(h, t) }
}

fun <A> concatenate(list: FunctionalList<FunctionalList<A>>): FunctionalList<A> {
    return foldRight(list, FunctionalList.empty()) { a, b -> append(a, b) }
}

fun addOne(list: FunctionalList<Int>): FunctionalList<Int> =
    foldRight(list, FunctionalList.empty()) { h, t -> Cons(h + 1, t) }

fun doubleToString(numbers: FunctionalList<Double>): FunctionalList<String> =
    foldRight(numbers, FunctionalList.empty()) { h, t -> Cons(h.toString(), t) }

fun <A, B> map(list: FunctionalList<A>, f: (A) -> B): FunctionalList<B> =
    foldRight(list, FunctionalList.empty()) { h, t -> Cons(f(h), t) }
