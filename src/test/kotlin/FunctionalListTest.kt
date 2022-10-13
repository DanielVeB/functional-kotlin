import Nil.drop
import Nil.dropWhile
import Nil.setHead
import Nil.tail
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FunctionalListTest {

    private val list = FunctionalList.of(1, 2, 3, 4, 5)

    @Test
    fun `Should return tail of list`() {
        assertEquals(FunctionalList.of(2, 3, 4, 5), list.tail())
    }

    @Test
    fun `Should set new head`() {
        val newList = list.setHead(0)
        assertEquals(FunctionalList.of(0, 1, 2, 3, 4, 5), newList)
    }

    @Test
    fun `Should set head with empty tail when added head to empty list`() {
        val nil = Nil
        val newList = nil.setHead(0)
        assertEquals(FunctionalList.of(0), newList)
    }

    @Test
    fun `Should drop first 3 elements from list`() {
        val newList = list.drop(3)

        assertEquals(FunctionalList.of(4, 5), newList)
    }

    @Test
    fun `Should drop all elements while first element is greater than 3`() {
        val newList = list.dropWhile { a -> a <= 3 }

        assertEquals(FunctionalList.of(4, 5), newList)
    }

    @Test
    fun `Should drop all elements when predicate is not satisfied`() {
        val newList = list.dropWhile { a -> a < 7 }
        assertEquals(Nil, newList)
    }

    @Test
    fun `Should calculate sum of numbers using fold expression`() {
        assertEquals(15, sumFold(list))
    }

    @Test
    fun `Should calculate product of numbers using fold expression`() {
        assertEquals(6.0, productFold(FunctionalList.of(1.0, 2.0, 3.0)))
    }

    @Test
    fun `Test pass Nil and Cons to foldRight`() {
        val l = Cons(1, Cons(2, Cons(3, Nil)))
        val result = foldRight(l, empty<Int>()) { x, y -> Cons(x, y) }

        assertEquals(l, result)
    }

    @Test
    fun `Should return length of list`(){
        val result = length(list)
        assertEquals(5, result)
    }
    @Test
    fun `Should return 0 length of Nil`(){
        val result = length(Nil)
        assertEquals(0, result)
    }

    @Test
    fun `xs`(){
        val list = FunctionalList.of(*((1..5000).toList().toTypedArray()))
        assertThrows(StackOverflowError::class.java) { sumFold(list) }

    }


}