package pure

import datastructures.Cons
import datastructures.List
import datastructures.foldRight


typealias State<S, A> = (S) -> Pair<A, S>

fun <S, A> unitP(a: A): State<S, A> = { rng -> a to rng }

fun <S, A, B> flatMap(f: State<S, A>, g: (A) -> State<S, B>): State<S, B> =
    { rng ->
        val (a, rng2) = f(rng)
        g(a).invoke(rng2)
    }

fun <S, A, B> map(s: State<S, A>, f: (A) -> B): State<S, B> =
    flatMap(s) { unitP(f.invoke(it)) }

fun <S, A, B, C> map2(ra: State<S, A>, rb: State<S, B>, f: (A, B) -> C): State<S, C> =
    flatMap(ra) { a ->
        map(rb) { b ->
            f(a, b)
        }
    }

fun <S, A> sequence(fs: List<State<S, A>>): State<S, List<A>> =
    foldRight(fs, unitP(List.empty())) { f: State<S, A>, acc: State<S, List<A>> ->
        map2(f, acc) { a, b -> Cons(a, b) }
    }
