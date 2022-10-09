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


