package strictnessandlaziness

import datastructures.List
import datastructures.Nil
import datastructures.reverse
import errorhandling.*

fun maybeTwice2(b: Boolean, i: () -> Int) {
    val j: Int by lazy(i)
    println("Call if statement")
    if (b) j + j
}

sealed class Stream<out A> {
    companion object {
        fun <A> of(vararg xs: A): Stream<A> =
            if (xs.isEmpty()) empty()
            else cons({ xs[0] },
                { of(*xs.sliceArray(1 until xs.size)) })
    }
}

data class Cons<out A>(
    val head: () -> A,
    val tail: () -> Stream<A>
) : Stream<A>()

object Empty : Stream<Nothing>()

fun <A> cons(hd: () -> A, tl: () -> Stream<A>): Stream<A> {
    val head: A by lazy(hd)
    val tail: Stream<A> by lazy(tl)
    return Cons({ head }, { tail })
}

fun <A> empty(): Stream<A> = Empty

fun <A> Stream<A>.headOption(): Option<A> =
    when (this) {
        is Empty -> None
        is Cons -> Some(head())
    }

fun <A> Stream<A>.toListUnsafe(): List<A> =
    when (this) {
        is Empty -> Nil
        is Cons -> datastructures.Cons(this.head(), tail().toListUnsafe())
    }

fun <A> Stream<A>.toList(): List<A> {
    tailrec fun <A> go(stream: Stream<A>, acc: List<A>): List<A> = when (stream) {
        is Empty -> acc
        is Cons -> go(stream.tail(), datastructures.Cons(stream.head(), acc))
    }
    return reverse(go(this, Nil))
}

fun <A> Stream<A>.take(n: Int): Stream<A> {
    fun <A> go(stream: Stream<A>, num: Int): Stream<A> = when (stream) {
        is Empty -> empty()
        is Cons -> {
            if (num == 0) empty()
            else cons(stream.head) { go(stream.tail(), num - 1) }
        }
    }
    return go(this, n)
}

fun <A> Stream<A>.takeWhileRec(p: (A) -> Boolean): Stream<A> {
    fun <A> go(stream: Stream<A>, p: (A) -> Boolean): Stream<A> = when (stream) {
        is Empty -> empty()
        is Cons -> {
            val h = stream.head()
            if (!p(h)) empty()
            else cons(stream.head) { go(stream.tail(), p) }
        }
    }
    return go(this, p)
}

fun <A> Stream<A>.drop(n: Int): Stream<A> {
    fun <A> go(stream: Stream<A>, num: Int): Stream<A> = when (stream) {
        is Empty -> empty()
        is Cons -> {
            if (num == 0) stream
            else go(stream.tail(), num - 1)
        }
    }
    return go(this, n)
}

fun <A, B> Stream<A>.foldRight(
    z: () -> B,
    f: (A, () -> B) -> B
): B = when (this) {
    is Cons -> f(this.head()) {
        tail().foldRight(z, f)
    }

    is Empty -> z()
}


fun <A> Stream<A>.exists(p: (A) -> Boolean): Boolean =
    when (this) {
        is Cons -> p(this.head()) || this.tail().exists(p)
        else -> false
    }

fun <A> Stream<A>.exists2(p: (A) -> Boolean): Boolean =
    foldRight({ false }, { a, b -> p(a) || b() })

fun <A> Stream<A>.forAll(p: (A) -> Boolean): Boolean =
    foldRight({ true }, { a, b -> p(a) && b() })

fun <A> Stream<A>.takeWhile(p: (A) -> Boolean): Stream<A> =
    foldRight({ empty() }) { a, b -> if (p(a)) cons({ a }, b) else empty() }

fun <A> Stream<A>.headOption2(): Option<A> =
    foldRight({ Option.empty() }, { a, _ -> Some(a) })

fun <A, B> Stream<A>.map(f: (A) -> B): Stream<B> =
    foldRight({ empty() }) { h, t -> cons({ f(h) }, t) }

fun <A> Stream<A>.filter(f: (A) -> Boolean): Stream<A> =
    foldRight({ empty() }) { h, t -> if (f(h)) cons({ h }, t) else t() }

fun <A> Stream<A>.append(other: () -> Stream<A>): Stream<A> =
    foldRight(other) { h, t -> cons({ h }, t) }

fun <A, B> Stream<A>.flatMap(f: (A) -> Stream<B>): Stream<B> =
    foldRight({ empty() }) { h, t -> f(h).append(t) }

fun ones(): Stream<Int> = cons({ 1 }, { ones() })

// DON'T DO THIS: will throw StackOverflowError - onesL will try to produce infinite list of integers
fun onesL(): List<Int> = datastructures.Cons(1, onesL())

fun <A> constant(a: A): Stream<A> = cons({ a }, { constant(a) })

fun from(n: Int): Stream<Int> = cons({ n }, { from(n + 1) })

fun fibs(): Stream<Int> {
    fun seed(n1: Int, n2: Int): Stream<Int> =
        cons({ n1 }, { seed(n2, n1 + n2) })
    return seed(0, 1)
}

fun <A, S> unfold(z: S, f: (S) -> Option<Pair<A, S>>): Stream<A> =
    f(z).map {
        cons({ it.first }, { unfold(it.second, f) })
    }.getOrElse { empty() }

fun onesUnfold(): Stream<Int> = unfold(1) { Some(Pair(1, 1)) }

fun <A> constantUnfold(a: A): Stream<A> = unfold(a) { Some(Pair(a, a)) }

fun fromUnfold(n: Int): Stream<Int> = unfold(n) { Some(Pair(it, it + 1)) }

fun fibsUnfold(): Stream<Int> = unfold(Pair(0, 1)) { (s1, s2) -> Some(Pair(s1, Pair(s2, s1 + s2))) }

fun <A, B> Stream<A>.mapUnfold(f: (A) -> B): Stream<B> = unfold(this) { s: Stream<A> ->
    when (s) {
        is Cons -> Some(f(s.head()) to s.tail())
        else -> None
    }
}

fun <A> Stream<A>.takeUnfold(n: Int): Stream<A> = unfold(Pair(this, n)) { (s, num) ->
    when (s) {
        is Cons -> {
            if (num <= 0) None
            else Some(Pair(s.head(), Pair(s.tail(), num - 1)))
        }

        else -> None
    }
}

fun <A> Stream<A>.takeWhileUnfold(p: (A) -> Boolean): Stream<A> = unfold(this) { s ->
    when (s) {
        is Cons -> {
            if (!p(s.head())) None
            else Some(Pair(s.head(), s.tail()))
        }

        else -> None
    }
}

fun <A, B, C> Stream<A>.zipWith(
    that: Stream<B>,
    f: (A, B) -> C
): Stream<C> =
    unfold(Pair(this, that)) { (s1, s2) ->
        when (s1) {
            is Cons -> {
                when (s2) {
                    is Cons -> Some(Pair(f(s1.head(), s2.head()), Pair(s1.tail(), s2.tail())))
                    else -> None
                }
            }

            else -> None
        }
    }


fun <A, B> Stream<A>.zipAll(
    that: Stream<B>
): Stream<Pair<Option<A>, Option<B>>> =
    unfold(Pair(this, that)) { (s1, s2) ->
        when (s1) {
            is Cons -> {
                when (s2) {
                    is Cons -> Some(
                        Pair(
                            Pair(Some(s1.head()), Some(s2.head())),
                            Pair(s1.tail(), s2.tail())
                        )
                    )

                    else -> Some(
                        Pair(
                            Pair(Some(s1.head()), None),
                            Pair(s1.tail(), empty())
                        )
                    )
                }
            }

            else -> when (s2) {
                is Cons -> Some(
                    Pair(
                        Pair(None, Some(s2.head())),
                        Pair(empty(), s2.tail())
                    )
                )

                else -> None
            }
        }
    }

fun <A> Stream<A>.startsWith(that: Stream<A>): Boolean =
    that.zipAll(this).forAll {
        when (it.first) {
            is Some -> it.first == it.second
            else -> true
        }
    }
