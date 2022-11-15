package errorhandling

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
}