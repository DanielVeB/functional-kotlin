package errorhandling

sealed class Option<out A>

data class Some<out A>(val get: A): Option<A>()

object None : Option<Nothing>()


fun <A, B> Option<A>.map(f: (A) -> B): Option<B> =
    when(this){
        is Some -> Some(f(this.get))
        is None -> None
    }

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> =
    when(this){
        is Some -> f(this.get)
        is None -> None
    }

fun <A> Option<A>.getOrElse(default: () -> A): A =
    when(this){
        is Some -> this.get
        is None -> default()
    }

fun <A> Option<A>.orElse(ob: () -> Option<A>): Option<A> =
    this.map { Some(it) }.getOrElse(ob)