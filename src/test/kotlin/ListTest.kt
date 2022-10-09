import Nil.drop
import Nil.dropWhile
import Nil.setHead
import Nil.tail
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ListTest {

    private val list = List.of(1, 2, 3, 4, 5)

    @Test
    fun `Should return tail of list`(){
        assertEquals(List.of(2,3,4,5), list.tail())
    }

    @Test
    fun `Should set new head`() {
        val newList = list.setHead(0)

        assertEquals(List.of(0,1,2,3,4,5), newList)
    }

    @Test
    fun `Should set head with empty tail when added head to empty list`(){
        val nil = Nil
        val newList = nil.setHead(0)
        assertEquals(List.of(0), newList)
    }

    @Test
    fun `Should drop first 3 elements from list`(){
        val newList = list.drop(3)

        assertEquals(List.of(4,5), newList)
    }

    @Test
    fun `Should drop all elements while first element is greater than 3`(){
        val newList = list.dropWhile { a -> a <= 3 }

        assertEquals(List.of(4,5), newList)
    }

    @Test
    fun `Should drop all elements when predicate is not satisfied`(){
        val newList = list.dropWhile { a -> a < 7 }

        assertEquals(Nil, newList)
    }

}