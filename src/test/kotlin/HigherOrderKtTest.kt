import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HigherOrderKtTest {
    @Test
    fun `Partial application example`(){
        val repeatLowercaseCharacterFun = partialApp(10) { a: Int, b: Char -> b.lowercase().repeat(a) }
        val result = repeatLowercaseCharacterFun('B')
        assertEquals("bbbbbbbbbb", result)
    }

    @Test
    fun `Currying example`(){
        val sumFun = curry { a : Int, b: Int -> a + b }
        val multipleFun = curry{ a : Int, b: Int -> a * b }

        val sum = sumFun(3)(5)
        val product = multipleFun(3)(5)

        assertEquals(8,sum)
        assertEquals(15, product)
    }

    @Test
    fun `Uncurrying example`(){
        val sumFun = curry { a : Int, b: Int -> a + b }

        val sumFunUncurried = uncurry(sumFun)
        val sum = sumFunUncurried(3,5)

        assertEquals(8,sum)
    }

    @Test
    fun `Composition example`(){
        val multiplyBy2 = { a: Int -> a * 2}
        val add10 = {a: Int -> a+10}

        val composeFirstAdd = compose(multiplyBy2, add10)
        val composeFirstMultiply = compose(add10, multiplyBy2)

        assertEquals(30, composeFirstMultiply(10))
        assertEquals(40, composeFirstAdd(10))
    }
}

