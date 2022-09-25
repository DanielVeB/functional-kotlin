val <T> List<T>.tail: List<T>
    get() = drop(1)

val <T> List<T>.head: T
    get() = first()

fun <A> isSorted(list: List<A>, order: (A, A) -> Boolean): Boolean {
    tailrec fun loop(l: List<A>): Boolean =
        when {
            l.tail.isEmpty() -> true
            !order(l.head, l.tail.head) -> false
            else -> loop(l.tail)
        }
    return loop(list)
}

// Partial application
// function is being applied to some but not all of the arguments it requires
fun <A, B, C> partialApp(a: A, f: (A, B) -> C): (B) -> C = { b -> f(a, b) }

// Currying
// converts a function f of two arguments into a function with one argument that partially applies f.
fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C = { a -> { b -> f(a, b) } }

fun <A, B, C> uncurry(f: (A) -> (B) -> C): (A, B) -> C = { a, b -> f(a)(b) }

// Function composition
// feeds the output of one function to the input of another
fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C = { a -> f(g(a)) }


