// By Sebastian Raaphorst, 2023.

import java.io.File
import java.lang.StringBuilder
import kotlin.system.exitProcess

// The suggested 29 shades of gray.
const val Density29 = "Ñ@#W$9876543210?!abc;:+=-,._ "
const val Density29Ext = "_29"

// The 29 shades of grey as found by FontAnalyzer.
const val Density29Calculated = "@Ñ&æÄÜB½#dòêàñé5axTÎct(ª*¡;¯ "
const val Density29CalculatedExt = "_29c"

// The 58 shades of gray as found by FontAnalyzer.
const val Density58Calculated = "@MÑ©ÒÐæÅÄm8ÚBÇ6AXõböóÝ¥ÿûSV3úkhÞ¢y±fn]>v}1«ÍLjº³²*I¡~;'¯· "
const val Density58CalculatedExt = "_58c"

// Horizontally print each character this many times to approximately maintain aspect ratio with original image.
const val HorizontalMagnification = 2

data class Info(val fileName: String, val scale: Int, val reverse: Boolean = false)

fun main() {
    listOf(
        Triple(Density29, Density29Ext, 8),
        Triple(Density29Calculated, Density29CalculatedExt, 8),
        Triple(Density58Calculated, Density58CalculatedExt, 16)
    ).forEach { (density, densityExt, brightnessShift) ->

        val shiftedDensity = density + " ".repeat(brightnessShift)
        val converter = Mappings.createQuantizingEpimorphism(shiftedDensity.toList())
        val inverseConverter = Mappings.createQuantizingEpimorphism(shiftedDensity.reversed().toList())
        val converters = listOf(inverseConverter, converter)
        val extensions = listOf(".txt", "_reversed.txt")

        val files = listOf(
            Info("arrow", 6),
            Info("figue", 1),
            Info("gundham_tanaka", 6),
            Info("me", 6),
            Info("not_the_cat", 6),
            Info("omori", 6, true),
            Info("ringo", 1),
            Info("us", 6)
        )

        files.forEach { (fileName, scale, reverse) ->
            val inputFileURL = object {}.javaClass.classLoader.getResource("$fileName.png") ?: run {
                println("Could not find file: $fileName")
                exitProcess(1)
            }
            val inputFile = File(inputFileURL.toURI())

            val indices = if (reverse) 2 else 1
            (0 until indices).forEach { idx ->
                val canvas = Canvas.fromPNGScaled(inputFile, scale)
                    .apply(rgbDensitySplitter)
                    .apply(RGBReducers.luminance_BT709)
                    .apply(converters[idx])

                val outputFile = File(inputFile.parent, "$fileName$densityExt${extensions[idx]}")

                val builder = StringBuilder()
                canvas.data.forEach { row ->
                    row.forEach { builder.append("$it".repeat(HorizontalMagnification)) }
                    builder.append("\n")
                }

                outputFile.writeText(builder.toString())
            }
        }
    }
}
