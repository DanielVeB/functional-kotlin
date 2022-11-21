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

fun <A> Stream<A>.takeWhile(p: (A) -> Boolean): Stream<A> {
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
