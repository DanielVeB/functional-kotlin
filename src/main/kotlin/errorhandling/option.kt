package errorhandling

import datastructures.*
import datastructures.Nil.setHead
import kotlin.math.pow

sealed class Option<out A> {
    companion object {
        fun <A> empty(): Option<A> = None
    }
}

data class Some<out A>(val get: A) : Option<A>()

object None : Option<Nothing>()


fun <A, B> Option<A>.map(f: (A) -> B): Option<B> =
    when (this) {
        is Some -> Some(f(this.get))
        is None -> None
    }

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> =
    when (this) {
        is Some -> f(this.get)
        is None -> None
    }


fun <A> Option<A>.getOrElse(default: () -> A): A =
    when (this) {
        is Some -> this.get
        is None -> default()
    }

fun <A> Option<A>.orElse(ob: () -> Option<A>): Option<A> =
    this.map { Some(it) }.getOrElse(ob)

fun <A> Option<A>.filter(f: (A) -> Boolean): Option<A> =
    this.flatMap { a: A -> if (f(a)) Some(a) else None }

fun <A> Option<A>.filter2(f: (A) -> Boolean): Option<A> =
    when (this) {
        is None -> None
        is Some -> if (f(this.get)) this else None
    }


fun mean(xs: List<Double>): Option<Double> =
    if (xs.isEmpty()) None else Some(xs.sum() / xs.size)


fun variance(xs: List<Double>): Option<Double> =
    mean(xs).flatMap { m ->
        mean(xs.map { (it - m).pow(2) })
    }

fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> =
    { oa -> oa.map(f) }

fun <A, B, C> map2(a: Option<A>, b: Option<B>, f: (A, B) -> C): Option<C> =
    a.flatMap { x -> b.flatMap { y -> Some(f(x, y)) } }

fun <A> sequence(xs: datastructures.List<Option<A>>): Option<datastructures.List<A>> =
    foldRight(xs, Some(Nil)) { option: Option<A>, list: Option<datastructures.List<A>> ->
        map2(option, list) { item: A, l: datastructures.List<A> ->
            l.setHead(item)
        }
    }

fun <A> catchesO(a: () -> A): Option<A> =
    try {
        Some(a())
    } catch (e: Throwable) {
        None
    }

fun parseInts(xs: datastructures.List<String>): Option<datastructures.List<Int>> =
    sequence(map(xs) { str -> catchesO { str.toInt() } })


//fun <A, B> traverse(
//    xa: FunctionalList<A>,
//    f: (A) -> Option<B>
//): Option<FunctionalList<B>> = sequence(map(xa, f))

fun <A, B> traverse(
    xa: datastructures.List<A>,
    f: (A) -> Option<B>
): Option<datastructures.List<B>> = when (xa) {
    is Nil -> Some(Nil)
    is Cons -> map2(f(xa.head), traverse(xa.tail, f)) { h, t -> Cons(h, t) }
}

fun <A, B> traverse2(
    xa: datastructures.List<A>,
    f: (A) -> Option<B>
): Option<datastructures.List<B>> = when (xa) {
    is Nil -> Some(Nil)
    is Cons -> foldRight(xa, Some(Nil)) { h: A, t: Option<datastructures.List<B>> ->
        map2(f(h), t) { x, y -> Cons(x, y) }
    }
}



