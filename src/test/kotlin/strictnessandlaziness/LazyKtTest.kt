package strictnessandlaziness

import datastructures.List
import errorhandling.None
import errorhandling.Some
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import utils.round
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class LazyKtTest {

    @Test
    fun `Should call method only once and cache`() {
        maybeTwice2(true) { println("hi"); 1 + 41 }
    }

    interface ExpensiveClass {
        fun expensiveMethod(i: Int): String
    }

    @Test
    fun `NOT GOOD - twice computation`() {
        var counter = 0
        val mocked = mockk<ExpensiveClass>()
        every { mocked.expensiveMethod(any()) }.answers {
            counter++
            ""
        }

        val x = Cons({ mocked.expensiveMethod(54) }, { Empty })
        x.headOption()
        x.headOption()

        assertEquals(2, counter)
    }

    @Test
    fun `GOOD - memoizing and avoiding recomputation`() {
        var counter = 0
        val mocked = mockk<ExpensiveClass>()
        every { mocked.expensiveMethod(any()) }.answers {
            counter++
            ""
        }

        val x = cons({ mocked.expensiveMethod(54) }, { Empty })
        x.headOption()
        x.headOption()

        assertEquals(1, counter)

    }

    @Test
    fun `Should convert stream to list`() {
        val stream = Stream.of(1, 2, 3, 4, 5)

        assertEquals(List.of(1, 2, 3, 4, 5), stream.toList())
    }

    @Test
    fun `Should take first 3 elements of stream`() {
        val stream = Stream.of(1, 2, 3, 4, 5)
        val result = stream.take(3)
        assertEquals(Stream.of(1, 2, 3).toList(), result.toList())
    }

    @Test
    fun `Should take while elements are less than 4 of stream`() {
        val stream = Stream.of(1, 2, 3, 4, 5, 4, 2, 6)

        val resultRec = stream.takeWhileRec { it < 4 }
        val result = stream.takeWhile { it < 4 }

        assertEquals(Stream.of(1, 2, 3).toList(), resultRec.toList())
        assertEquals(Stream.of(1, 2, 3).toList(), result.toList())
    }

    @Test
    fun `Should drop first 3 elements of stream`() {
        val stream = Stream.of(1, 2, 3, 4, 5)
        val result = stream.drop(3)
        assertEquals(Stream.of(4, 5).toList(), result.toList())
    }


    @Test
    fun `Should return true for existing element`() {
        val stream = Stream.of(1, 2, 3, 4, 5)
        val result = stream.exists { it == 3 }
        assertTrue { result }

        val result2 = stream.exists2 { it == 3 }
        assertTrue { result2 }
    }

    @Test
    fun `Should return true when all elements match predicate`() {
        val stream = Stream.of(1, 2, 3, 4, 5)
        val result = stream.forAll { it < 6 }
        assertTrue { result }

        val result2 = stream.forAll { it == 1 }
        assertFalse { result2 }
    }

    @Test
    fun `Should transform double to string using map function`() {
        Assertions.assertEquals(
            Stream.of("1.0", "2.0", "3.0").toList(),
            Stream.of(1.0, 2.0, 3.0).map { a -> a.toString() }.toList()
        )
    }

    @Test
    fun `Should remove all numbers greater than 5`() {
        Assertions.assertEquals(
            Stream.of(1, 2, 3, 4, 5).toList(),
            Stream.of(1, 6, 2, 3, 72, 43, 11, 4, 5, 10).filter { it <= 5 }.toList()
        )
    }

    @Test
    fun `Should append 2 streams`() {
        val s1 = Stream.of(1, 2, 3)
        val s2 = Stream.of(4, 5, 6)
        Assertions.assertEquals(List.of(1, 2, 3, 4, 5, 6), s1.append { s2 }.toList())
    }

    @Test
    fun `Should flatten stream`() {
        val stream = Stream.of(1, 2, 3)
        Assertions.assertEquals(
            List.of(2, 3, 4, 6, 6, 9),
            stream.flatMap { Stream.of(it * 2, it * 3) }.toList()
        )
    }

    @Test
    fun `Should take first 4 elements from infinite stream`() {
        val result = ones().take(5).toList()
        assertEquals(List.of(1, 1, 1, 1, 1), result)

        assertDoesNotThrow { ones() }

        assertThrows<StackOverflowError> { onesL() }

    }

    @Test
    fun `Should take first 5 n+1 elements starting from 10`() {
        val result = from(10).take(5).toList()
        assertEquals(List.of(10, 11, 12, 13, 14), result)

    }

    @Test
    fun `Should take first 10 elements of fibbonaci`() {
        val result = fibs().take(10).toList()
        assertEquals(List.of(0, 1, 1, 2, 3, 5, 8, 13, 21, 34), result)

    }

    @Test
    fun `Unfold - Should take first 4 elements from infinite stream`() {
        val result = onesUnfold().take(5).toList()
        assertEquals(List.of(1, 1, 1, 1, 1), result)
    }

    @Test
    fun `Unfold - Should take first 5 n+1 elements starting from 10`() {
        val result = fromUnfold(10).take(5).toList()
        assertEquals(List.of(10, 11, 12, 13, 14), result)
    }

    @Test
    fun `Unfold - Should take first 10 elements of fibbonaci`() {
        val result = fibsUnfold().take(10).toList()
        assertEquals(List.of(0, 1, 1, 2, 3, 5, 8, 13, 21, 34), result)

    }

    @Test
    fun `Unfold - Should transform double to string using map function`() {
        Assertions.assertEquals(
            Stream.of("1.0", "2.0", "3.0").toList(),
            Stream.of(1.0, 2.0, 3.0).mapUnfold { a -> a.toString() }.toList()
        )
    }

    @Test
    fun `Unfold - Should take first 3 elements of stream`() {
        val stream = Stream.of(1, 2, 3, 4, 5)
        val result = stream.takeUnfold(3)
        assertEquals(Stream.of(1, 2, 3).toList(), result.toList())
    }

    @Test
    fun `Unfold - Should take while elements are less than 4 of stream`() {
        val stream = Stream.of(1, 2, 3, 4, 5, 4, 2, 6)

        val result = stream.takeWhileUnfold { it < 4 }

        assertEquals(Stream.of(1, 2, 3).toList(), result.toList())
    }

    @Test
    fun `Unfold - Should divide elements of first stream with elements of second`() {
        val s1 = Stream.of(1, 3, 4)
        val s2 = Stream.of(2, 4, 6)

        Assertions.assertEquals(
            List.of(0.5, 0.75, 0.67),
            s1.zipWith(s2) { a, b -> a.toDouble().div(b).round(2) }.toList()
        )
    }

    @Test
    fun `Unfold - Should zip pairs of two streams`() {
        val s1 = Stream.of('a', 'b', 'c')
        val s2 = Stream.of(1, 2, 3)

        Assertions.assertEquals(
            List.of(
                Pair(Some('a'), Some(1)),
                Pair(Some('b'), Some(2)),
                Pair(Some('c'), Some(3))
            ),
            s1.zipAll(s2).toList()
        )

        val s3 = Stream.of('a', 'b', 'c')
        val s4 = Stream.of(1, 2)

        Assertions.assertEquals(
            List.of(
                Pair(Some('a'), Some(1)),
                Pair(Some('b'), Some(2)),
                Pair(Some('c'), None)
            ),
            s3.zipAll(s4).toList()
        )

        val s5 = Stream.of('a', 'b')
        val s6 = Stream.of(1, 2, 3)

        Assertions.assertEquals(
            List.of(
                Pair(Some('a'), Some(1)),
                Pair(Some('b'), Some(2)),
                Pair(None, Some(3))
            ),
            s5.zipAll(s6).toList()
        )

        val s7 = Stream.of('a', 'b', 'c')
        Assertions.assertEquals(
            List.of(
                Pair(Some('a'), None),
                Pair(Some('b'), None),
                Pair(Some('c'), None)
            ),
            s7.zipAll(empty<Int>()).toList()
        )

        val s8 = Stream.of(1, 2, 3)

        Assertions.assertEquals(
            List.of(
                Pair(None, Some(1)),
                Pair(None, Some(2)),
                Pair(None, Some(3))
            ),
            empty<String>().zipAll(s8).toList()
        )

    }
}