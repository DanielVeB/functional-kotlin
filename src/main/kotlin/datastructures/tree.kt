package datastructures

sealed class Tree<out A>

data class Leaf<A>(val value: A): Tree<A>()
data class Branch<A>(val left: Tree<A>, val right: Tree<A>): Tree<A>()

fun <A> Tree<A>.size(): Int {
    return when(this){
        is Leaf -> 1
        is Branch -> this.left.size() + 1 + this.right.size()
    }
}

fun Tree<Int>.maximum(): Int {
    return when(this){
        is Leaf -> this.value
        is Branch -> maxOf(this.left.maximum(), this.right.maximum())
    }
}

fun <A> Tree<A>.depth(): Int {
    return when(this){
        is Leaf -> 0
        is Branch -> maxOf(this.left.depth(), this.right.depth()) + 1
    }
}


fun <A, B> Tree<A>.map(f: (A) -> B): Tree<B> =
    when(this){
        is Leaf -> Leaf(f(this.value))
        is Branch -> Branch(this.left.map(f),this.right.map(f))
    }


