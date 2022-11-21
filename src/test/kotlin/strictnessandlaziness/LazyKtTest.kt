package strictnessandlaziness

import datastructures.List
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
        val stream = Stream.of(1, 2, 3, 4, 5, 6)
        val result = stream.takeWhile { it < 4 }
        assertEquals(Stream.of(1, 2, 3).toList(), result.toList())
    }

    @Test
    fun `Should drop first 3 elements of stream`() {
        val stream = Stream.of(1, 2, 3, 4, 5)
        val result = stream.drop(3)
        assertEquals(Stream.of(4, 5).toList(), result.toList())
    }
}