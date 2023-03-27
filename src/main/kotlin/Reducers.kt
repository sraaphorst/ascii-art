// By Sebastian Raaphorst, 2023.

import Reducers.toLongs
import kotlin.streams.toList

object Reducers {
    // A function that takes an ImageChunk<T> and a function T -> Double
    // and then uses parallel computing to average out the values in the chunk down to a Double.
    fun <T> averageChunk(imageChunk: ImageChunk<T>, mapping: (T) -> Double): Double =
        imageChunk
            .stream()
            .parallel()
            .map{ it.map(mapping).sum() }
            .toList()
            .sum() / (imageChunk.size * imageChunk[0].size)

    // Split an Int representing an RGB value in [0x000000, 0xffffff] into its constituent components,
    // convert to Long, sum the constituent components, and then take the average and collapse back down to an
    // RGB Int.
    // Note that parallel computing is used over the rows.
    fun averageRGBChunk(imageChunk: ImageChunk<Int>): Int =
        rgbCombiner(imageChunk
            .stream()
            .parallel()
            .map { rgbRow ->
                val rgbTriples = rgbRow.map { rgb ->
                    rgbSplitter(rgb)
                }
                rgbTriples.toLongs().sum()
            }.toList()
            .sum() / (imageChunk.size * imageChunk[0].size))

    private fun List<Triple<Int, Int, Int>>.toLongs(): List<Triple<Long, Long, Long>> =
        map { Triple(it.first.toLong(), it.second.toLong(), it.third.toLong()) }

    private fun List<Triple<Long, Long, Long>>.sum(): Triple<Long, Long, Long> =
        this.fold(Triple(0L, 0L,0L)) { (r1, g1, b1), (r2, g2, b2) ->
            Triple(r1 + r2, g1 + g2, b1 + b2)
        }

    private operator fun Triple<Long, Long, Long>.div(dividend: Int): Triple<Int, Int, Int> =
        Triple(this.first.div(dividend).toInt(), this.second.div(dividend).toInt(), this.third.div(dividend).toInt())
}
