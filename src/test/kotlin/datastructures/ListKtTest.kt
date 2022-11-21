package datastructures
import datastructures.Nil.drop
import datastructures.Nil.dropWhile
import datastructures.Nil.setHead
import datastructures.Nil.tail
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import utils.round

internal class ListKtTest {

    private val list = List.of(1, 2, 3, 4, 5)

    @Test
    fun `Should return tail of list`() {
        assertEquals(List.of(2, 3, 4, 5), list.tail())
    }

    @Test
    fun `Should set new head`() {
        val newList = list.setHead(0)
        assertEquals(List.of(0, 1, 2, 3, 4, 5), newList)
    }

    @Test
    fun `Should set head with empty tail when added head to empty list`() {
        val nil = Nil
        val newList = nil.setHead(0)
        assertEquals(List.of(0), newList)
    }

    @Test
    fun `Should drop first 3 elements from list`() {
        val newList = list.drop(3)

        assertEquals(List.of(4, 5), newList)
    }

    @Test
    fun `Should drop all elements while first element is greater than 3`() {
        val newList = list.dropWhile { a -> a <= 3 }

        assertEquals(List.of(4, 5), newList)
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
        assertEquals(6.0, productFold(List.of(1.0, 2.0, 3.0)))
    }

    @Test
    fun `Should calculate sum of numbers using left fold expression`() {
        assertEquals(15, sumLeftFold(list))
    }

    @Test
    fun `Should calculate product of numbers using left fold expression`() {
        assertEquals(6.0, productLeftFold(List.of(1.0, 2.0, 3.0)))
    }

    @Test
    fun `Test pass Nil and Cons to foldRight`() {
        val l = Cons(1, Cons(2, Cons(3, Nil)))
        val result = foldRight(l, List.empty<Int>()) { x, y -> Cons(x, y) }

        assertEquals(l, result)
    }

    @Test
    fun `Should return length of list`() {
        val result = length(list)
        assertEquals(5, result)
    }

    @Test
    fun `Should return 0 length of Nil`() {
        val result = length(Nil)
        assertEquals(0, result)
    }

    @Test
    fun `Should return reversed list`() {
        assertEquals(List.of(5, 4, 3, 2, 1), reverse(list))
    }

    @Test
    fun `Should append 2 lists`() {
        val l1 = List.of(1, 2, 3)
        val l2 = List.of(4, 5, 6)
        assertEquals(List.of(1, 2, 3, 4, 5, 6), append(l1, l2))
    }

    @Test
    fun `Should concatenate list of lists`() {
        val p1 = List.of(1, 2)
        val p2 = List.of(3, 4)
        val p3 = List.of(5, 6)

        assertEquals(List.of(1, 2, 3, 4, 5, 6), concatenate(List.of(p1, p2, p3)))
    }

    @Test
    fun `Should add 1 to each element in list`() {
        assertEquals(List.of(2, 3, 4, 5, 6), addOne(list))
    }

    @Test
    fun `Should transform double to string`() {
        assertEquals(
            List.of("1.0", "2.0", "3.0"),
            doubleToString(List.of(1.0, 2.0, 3.0))
        )
    }

    @Test
    fun `Should add 1 to each element in list using map function`() {
        assertEquals(List.of(2, 3, 4, 5, 6), map(list) { a -> 1 + a })
    }

    @Test
    fun `Should transform double to string using map function`() {
        assertEquals(
            List.of("1.0", "2.0", "3.0"),
            map(List.of(1.0, 2.0, 3.0)) { a -> a.toString() }
        )
    }

    @Test
    fun `Should remove all numbers greater than 5`() {
        assertEquals(List.of(1, 2, 3, 4, 5),
            filter(List.of(1, 6, 2, 3, 72, 43, 11, 4, 5, 10)) { it <= 5 })

        assertEquals(List.of(1, 2, 3, 4, 5),
            filterWithFlatMap(List.of(1, 6, 2, 3, 72, 43, 11, 4, 5, 10)) { it <= 5 })
    }

    @Test
    fun `Should do copy of each element in list`() {
        assertEquals(List.of(1, 1, 2, 2, 3, 3),
            flatMap(List.of(1, 2, 3)) { List.of(it, it) })

        assertEquals(List.of(1, 1, 2, 2, 3, 3),
            flatMap2(List.of(1, 2, 3)) { List.of(it, it) })
    }

    @Test
    fun `Should construct new list by adding corresponding elements`() {
        assertEquals(
            List.of(5, 7, 9),
            zipWithSum(List.of(1, 2, 3), List.of(4, 5, 6))
        )

        assertEquals(List.of(5, 7, 9),
            zipWith(List.of(1, 2, 3), List.of(4, 5, 6)) { a, b -> a + b })
    }

    @Test
    fun `test zipWith`() {
        val l1 = List.of(1, 3, 4)
        val l2 = List.of(2, 4, 6)

        assertEquals(List.of(0.5, 0.75, 0.67), zipWith(l1, l2) { a, b -> a.toDouble().div(b).round(2) })

    }


}