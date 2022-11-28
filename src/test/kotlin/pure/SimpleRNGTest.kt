package pure

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SimpleRNGTest {

    @Test
    fun `Random method should always return the same output`() {
        val rng = SimpleRNG(42)
        val (n1, _) = rng.nextInt()
        val (n2, _) = rng.nextInt()
        val (n3, _) = rng.nextInt()

        assertEquals(n1, n2)
        assertEquals(n2, n3)
        assertEquals(16159453, n1)
    }

    @Test
    fun `Should return nonnegative random integer`() {
        val result = nonNegativeInt(SimpleRNG(5432321))
        assertTrue(result.first > 0)
    }

    @Test
    fun `Should return random double`() {
        val result = double(SimpleRNG(5432321))
        assertTrue(result.first > 0)
    }

    @Test
    fun `Should return random double - with map function`() {
        val randomDouble = doubleR()
        assertTrue(randomDouble(SimpleRNG(5432321)).first > 0)
    }

    @Test
    fun `Should generate list of random ints`() {
        val rng = SimpleRNG(42)

        val r1 = ints(5, rng)
        val r2 = ints2(5, rng)
        assertEquals(r1, r2)
    }

}