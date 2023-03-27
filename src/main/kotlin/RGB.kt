// By Sebastian Raaphorst, 2023.

import kotlin.math.max
import kotlin.math.min

typealias RGBHex = Triple<Int, Int, Int>
typealias RGBDoubles = Triple<Double, Double, Double>
typealias RGBReducer = (RGBDoubles) -> Double

// Split an RGB value [0x000000, 0xffffff] into [0, 0xff]^3.
val rgbSplitter: (Int) -> RGBHex = { rgb: Int ->
    Triple((rgb shr 16) and 0xff, (rgb shr 8) and 0xff, rgb and 0xff)
}

// Combine an RGB [0xff]^3 into an RGB value in [0x000000, 0xffffff].
val rgbCombiner: (RGBHex) -> Int = { (r, g, b) ->
    ((r shl 16) and 0xff0000) or ((g shl 8) and 0xff00) or (b and 0xff)
}

// Convert a value [0x00, 0xff] to [0, 1].
val hexToDouble: (Int) -> Double = { c: Int -> c / 255.0 }

// Split an RGB value [0x000000, 0xffffff] into [0, 1]^3.
val rgbDensitySplitter: (Int) -> RGBDoubles = { rgb: Int ->
    val (r, g, b) = rgbSplitter(rgb)
    Triple(hexToDouble(r), hexToDouble(g), hexToDouble(b))
}

object RGBReducers {
    // Given the coefficients for r, g, and b, which should add up to 1, make an RGBReducer.
    private val luminanceGenerator: (Double, Double, Double) -> RGBReducer = { rIntensity, gIntensity, bIntensity ->
        { (r, g, b) ->
            rIntensity * r + gIntensity * g + bIntensity * b
        }
    }

    // Functions to take an RGB value and convert it to a density in [0,1].
    val averageRGB: RGBReducer = { (r, g, b) -> (r + g + b) / 3.0 }
    val luminance: RGBReducer = luminanceGenerator(0.3, 0.59, 0.11)
    val luminance_BT709: RGBReducer = luminanceGenerator(0.2126, 0.7152, 0.0722)
    val luminance_BT601: RGBReducer = luminanceGenerator(0.299, 0.587, 0.114)
    val maxRGB: RGBReducer = { (r, g, b) -> max(r, max(g, b)) }
    val minRGB: RGBReducer = { (r, g, b) -> min(r, min(g, b)) }
    val desaturation: RGBReducer = { c -> (maxRGB(c) + minRGB(c)) / 2.0 }
    val redProjection: RGBReducer = { it.first }
    val greenProjection: RGBReducer = { it.second }
    val blueProjection: RGBReducer = { it.third }
}
