package strictnessandlaziness

import datastructures.List
import datastructures.Nil
import datastructures.reverse
import errorhandling.None
import errorhandling.Option
import errorhandling.Some

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


