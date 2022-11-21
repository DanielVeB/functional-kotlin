package errorhandling

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import utils.sumIntegers
import java.lang.Exception

internal class EitherKtTest {

    @Test
    fun `Should divide 10 by given value`() {

        assertEquals(Right(5), Right(2).map { 10 / it })
        assertEquals(Right(10), Right(1).map { 10 / it })
        assertEquals(Right(1), Right(10).map { 10 / it })

    }

    @Test
    fun `Should return the valid outcome of the whole flatMap chain`() {

        assertEquals(Right(5),
            Right(1)
                .flatMap { Right(2) }
                .flatMap { Right(3) }
                .flatMap { Right(4) }
                .flatMap { Right(5) })
    }

    @Test
    fun `Should return the first of the encountered error`() {

        assertTrue(Right(1)
            .flatMap { Right(2) }
            .flatMap { Right(3) }
            .flatMap { Left(Exception()) }
            .flatMap { Right(5) } is Left)
    }
    @Test
    fun `Should calculate sum of integers when both vales are right`() {
        assertEquals(Right(10), map2(Right(3), Right(7), sumIntegers))
    }

    @Test
    fun `Should return Left when either of values is left`() {
        assertTrue(map2(Left(Exception()), Right(7), sumIntegers) is Left)
    }

}