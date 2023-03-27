// By Sebastian Raaphorst, 2023.

import java.io.File
import java.net.URL
import kotlin.system.exitProcess

const val Density = "Ñ@#W$9876543210?!abc;:+=-,._ "

fun main() {
    val brightnessShift = 8
    val horizontalMagnification = 2
    val scaleFactor = 6

    val filename = "arrow.png"
    val fileUrl: URL = object {}.javaClass.classLoader.getResource(filename) ?: run {
        println("Could not find file: $filename")
        exitProcess(1)
    }
    val file = File(fileUrl.toURI())

    val shiftedDensity = Density + " ".repeat(brightnessShift)
    val converter = Mappings.createQuantizingEpimorphism(shiftedDensity.toList())
    val inverseConverter = Mappings.createQuantizingEpimorphism(shiftedDensity.reversed().toList())

    val canvas = Canvas.fromPNGScaled(file, scaleFactor)
        .apply(rgbDensitySplitter)
        .apply(RGBReducers.luminance_BT709)
//        .apply(RGBReducers.averageRGB)
        .apply(inverseConverter)
//        .apply(converter)

    canvas.data.forEach { row ->
        row.forEach {
            print("$it".repeat(horizontalMagnification))
        }
        println()
    }
}
