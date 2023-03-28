// By Sebastian Raaphorst, 2023.

import java.awt.Font
import java.awt.image.BufferedImage
import kotlin.math.round

class FontAnalyzer(fontName: String) {
    private val font = Font(fontName, Font.PLAIN, size)

    private fun determineDensity(c: Char): Double {
        val image = BufferedImage(bufferParameter, bufferParameter, BufferedImage.TYPE_INT_ARGB)
        val g = image.graphics
        g.font = font
        g.drawString(c.toString(), 0, size)
        g.dispose()

        val pixelsUsed = (0 until image.height).sumOf { x ->
            (0 until image.width).count { y ->
                image.getRGB(x, y) != 0
            }
        }

        return pixelsUsed.toDouble() / (image.width * image.height)
    }

    // All characters but the space, which is included separately, and the ISO control characters.
    private val glyphsByDensity = (StartRange..EndRange)
        .map(Int::toChar)
        .filterNot(Char::isISOControl)
        .associateWith(::determineDensity)
        .toList()
        .filterNot { it.second == 0.0 }
        .sortedBy { it.second }
        .reversed()

    fun glyphsForBins(numBins: Int): List<Char> {
        val stepSize = glyphsByDensity.size.toDouble() / (numBins - 1)
        return (0 until (numBins - 1)).map {
            val idx = round((it * stepSize)).toInt()
            glyphsByDensity[idx].first
        }.toList() + ' '
    }

    companion object {
        private const val StartRange = 32
        private const val EndRange = 0xff
        private const val size = 24
        private const val bufferParameter = 32
    }
}

fun main() {
    val f = FontAnalyzer("JetBrains Mono")

    fun showBins(numBins: Int) {
        val glyphs = f.glyphsForBins(numBins)
        val glyphString = glyphs.joinToString(separator = "", prefix = "\"", postfix = "\"")
        println("$numBins quantization: $glyphString")
    }

    // Display the characters with their density.
//    println("*** Glyphs by density: ***")
//    f.glyphsByDensity.withIndex().forEach {
//        println("${it.index} ${it.value.first} ${it.value.second}")
//    }

    showBins(29)
    showBins(58)
}
