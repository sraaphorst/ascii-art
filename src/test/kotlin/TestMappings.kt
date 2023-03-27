// By Sebastian Raaphorst, 2023.

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestMappings {
    @Test
    fun `Mapping with 16 values should invert`() {
        val range = (0 until 16).toList()
        val m = Mappings.createInvertibleQuantizedMapping(range)
        range.forEach {
            assertEquals(it, m.map(m.inverseMap(it)))
        }
    }

    @Test
    fun `Mapping to binary should invert`() {
        val range = listOf(0, 1)
        val m = Mappings.createInvertibleQuantizedMapping(range)
        range.forEach {
            assertEquals(it, m.map(m.inverseMap(it)))
        }
    }
}
