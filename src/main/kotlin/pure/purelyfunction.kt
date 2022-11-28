package pure

import datastructures.Cons
import datastructures.List
import datastructures.Nil
import datastructures.foldRight
import errorhandling.None
import errorhandling.Some
import errorhandling.getOrElse
import strictnessandlaziness.*

interface RNG {
    fun nextInt(): Pair<Int, RNG>
}

data class SimpleRNG(val seed: Long) : RNG {
    override fun nextInt(): Pair<Int, RNG> {
        val newSeed =
            (seed * 0x5DEECE66DL + 0xBL) and
                    0xFFFFFFFFFFFFL
        val nextRNG = SimpleRNG(newSeed)
        val n = (newSeed ushr 16).toInt()
        return n to nextRNG
    }
}

fun nonNegativeInt(rng: RNG): Pair<Int, RNG> {
    val (n, rng2) = rng.nextInt()
    return (if (n < 0) -(n + 1) else n) to rng2
}

fun double(rng: RNG): Pair<Double, RNG> {
    val (n, rng2) = nonNegativeInt(rng)
    return (n / (Int.MAX_VALUE.toDouble() + 1)) to rng2
}

fun intDouble(rng: RNG): Pair<Pair<Int, Double>, RNG> {
    val (num, rng2) = nonNegativeInt(rng)
    val (dnum, rng3) = double(rng2)
    return (num to dnum) to rng3
}

fun doubleInt(rng: RNG): Pair<Pair<Double, Int>, RNG> {
    val (id, rng2) = intDouble(rng)
    val (inum, dnum) = id
    return (dnum to inum) to rng2

}

fun double3(rng: RNG): Pair<Triple<Double, Double, Double>, RNG> {
    val (d1, rng2) = double(rng)
    val (d2, rng3) = double(rng2)
    val (d3, rng4) = double(rng3)
    return Triple(d1, d2, d3) to rng4
}

fun ints(count: Int, rng: RNG): Pair<List<Int>, RNG> {
    val stream = unfold(Pair(count, rng)) { (n, r) ->
        if (n > 0) {
            val (n2, r2) = nonNegativeInt(r)
            Some((n2 to r2) to (n - 1 to r2))
        } else None
    }
    val l = stream.map { it.first }.toList()
    val r = stream.drop(count - 1).headOption().getOrElse { 0 to rng }.second
    return l to r
}

fun ints2(count: Int, rng: RNG): Pair<List<Int>, RNG> =
    if (count > 0) {
        val (i, r1) = rng.nextInt()
        val (xs, r2) = ints(count - 1, r1)
        Cons(i, xs) to r2
    } else Nil to rng

//--------------------------------------------------------

typealias Rand<A> = (RNG) -> Pair<A, RNG>

val intR: Rand<Int> = { rng -> rng.nextInt() }
fun <A> unit(a: A): Rand<A> = { rng -> a to rng }

fun <A, B> map(s: Rand<A>, f: (A) -> B): Rand<B> =
    { rng ->
        val (a, rng2) = s(rng)
        f(a) to rng2
    }

fun nonNegativeEven(): Rand<Int> =
    map(::nonNegativeInt) { it - (it % 2) }

fun doubleR(): Rand<Double> =
    map(::nonNegativeInt) { it / (Int.MAX_VALUE.toDouble() + 1) }

fun <A, B, C> map2(
    ra: Rand<A>,
    rb: Rand<B>,
    f: (A, B) -> C
): Rand<C> = { rng ->
    val (a, rnga) = ra(rng)
    val (b, rngb) = rb(rnga)
    f(a, b) to rngb
}

fun <A, B> both(ra: Rand<A>, rb: Rand<B>): Rand<Pair<A, B>> =
    map2(ra, rb) { a, b -> a to b }


val doubleR: Rand<Double> =
    map(::nonNegativeInt) { i ->
        i / (Int.MAX_VALUE.toDouble() + 1)
    }

val intDoubleR: Rand<Pair<Int, Double>> = both(intR, doubleR)

val doubleIntR: Rand<Pair<Double, Int>> = both(doubleR, intR)

fun <A> sequence(fs: List<Rand<A>>): Rand<List<A>> =
    foldRight(fs, unit(List.empty())) { f: Rand<A>, acc: Rand<List<A>> ->
        map2(f, acc) { a, b -> Cons(a, b) }
    }