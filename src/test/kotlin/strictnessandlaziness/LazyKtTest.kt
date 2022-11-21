package strictnessandlaziness

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
        val h1 = x.headOption()
        val h2 = x.headOption()

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
        val h1 = x.headOption()
        val h2 = x.headOption()

        assertEquals(1, counter)

    }
}