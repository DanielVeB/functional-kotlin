import java.lang.Exception

sealed class List<out A>{

    companion object{
        fun<A> of(vararg aa: A): List<A>{
            val tail = aa.sliceArray(1 until aa.size)
            return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
        }
    }

    fun <A> List<A>.tail(): List<A>{
        return when(this){
            is Nil -> throw IllegalStateException("Nil cannot have tail")
            is Cons -> this.tail
        }
    }

    fun <A> List<A>.setHead(x: A): List<A>{
        return when(this){
            is Cons -> Cons(x, this.tail.setHead(this.head))
            is Nil -> Cons(x, Nil)
        }
    }

    fun <A> List<A>.drop(n: Int): List<A>{
        return if (n == 0 ) this
        else when(this){
            is Cons -> this.tail.drop(n-1)
            Nil -> throw Exception("List is too short")
        }
    }

    fun <A> List<A>.dropWhile(f: (A) -> Boolean): List<A>{
        return when(this){
            is Cons -> {
                if(f(this.head)) this.tail.dropWhile(f)
                else this
            }
            Nil -> Nil
        }

    }
}

object Nil: List<Nothing>()

data class Cons<out A>(val head: A, val tail: List<A>): List<A>()

fun sum(numbers: List<Int>): Int{
    return when(numbers){
        is Nil -> 0
        is Cons -> numbers.head + sum(numbers.tail)
    }
}
