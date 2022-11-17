package errorhandling

import datastructures.FunctionalList
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OptionKtTest {

    @Test
    fun `Should multiple by 2 when some value is provided`() {

        assertEquals(Some(4), Some(2).map { it * 2 })
        assertEquals(Some(0), Some(0).map { it * 2 })
        assertEquals(Some(-4), Some(-2).map { it * 2 })

        assertEquals(None, None.map { it })

    }

    @Test
    fun `Should return value or default`() {
        assertEquals(1, Some(1).getOrElse { Some(2) })
        assertEquals(2, None.getOrElse { 2 })
    }

    @Test
    fun `Should return option value or default option`() {
        assertEquals(Some(1), Some(1).orElse { Some(2) })
        assertEquals(Some(2), None.orElse { Some(2) })
    }

    @Test
    fun `Should return option value after filtering`() {
        assertEquals(Some(1), Some(1).filter { it % 2 == 1 })
        assertEquals(None, Some(2).filter { it % 2 == 1 })
    }

    @Test
    fun `Should calculate variance of list`() {
        assertEquals(Some(2.0), variance(listOf(1.0, 2.0, 3.0, 4.0, 5.0)))
    }

    @Test
    fun `Should map two options when neither is None`() {
        assertEquals(None, map2(Some(1), None) { a: Int, b: Int -> a + b })
        assertEquals(None, map2(None, None) { a: Int, b: Int -> a + b })
        assertEquals(None, map2(None, Some(1)) { a: Int, b: Int -> a + b })

        assertEquals(Some(3), map2(Some(2), Some(1)) { a: Int, b: Int -> a + b })

    }

    @Test
    fun `Should combine a list of Options int one Option containing a list`() {
        assertEquals(
            Some(FunctionalList.of(1, 2, 3)),
            sequence(FunctionalList.of(Some(1), Some(2), Some(3))))

        assertEquals(
            None,
            sequence(FunctionalList.of(Some(1), None, Some(3))))

    }


}