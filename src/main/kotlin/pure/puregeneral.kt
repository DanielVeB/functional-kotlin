package pure

import datastructures.Cons
import datastructures.List
import datastructures.foldRight


typealias PureState<S, A> = (S) -> Pair<A, S>

fun <S, A> unitP(a: A): PureState<S, A> = { rng -> a to rng }

fun <S, A, B> flatMap(f: PureState<S, A>, g: (A) -> PureState<S, B>): PureState<S, B> =
    { rng ->
        val (a, rng2) = f(rng)
        g(a).invoke(rng2)
    }

fun <S, A, B> map(s: PureState<S, A>, f: (A) -> B): PureState<S, B> =
    flatMap(s) { unitP(f.invoke(it)) }

fun <S, A, B, C> map2(
    ra: PureState<S, A>,
    rb: PureState<S, B>,
    f: (A, B) -> C
): PureState<S, C> =
    flatMap(ra) { a ->
        map(rb) { b ->
            f(a, b)
        }
    }

fun <S, A> sequence(fs: List<PureState<S, A>>): PureState<S, List<A>> =
    foldRight(fs, unitP(List.empty())) { f: PureState<S, A>, acc: PureState<S, List<A>> ->
        map2(f, acc) { a, b -> Cons(a, b) }
    }

