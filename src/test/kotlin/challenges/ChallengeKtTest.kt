package challenges

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ChallengeKtTest {

    //    396. Rotate Function
    @Test
    fun `Should return maximum value from rotation function`() {
        assertEquals(26, maxRotateFunction(listOf(4, 3, 2, 6)))
        assertEquals(0, maxRotateFunction(listOf(100)))

    }
}