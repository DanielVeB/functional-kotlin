package errorhandling

import datastructures.FunctionalList
import datastructures.Nil
import datastructures.Nil.setHead
import datastructures.foldRight
import kotlin.math.pow

sealed class Option<out A>

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

fun <A> sequence(xs: FunctionalList<Option<A>>): Option<FunctionalList<A>> =
    foldRight(xs, Some(Nil)) { option: Option<A>, list: Option<FunctionalList<A>> ->
        map2(option, list) { item: A, l: FunctionalList<A> ->
            l.setHead(item)
        }
    }