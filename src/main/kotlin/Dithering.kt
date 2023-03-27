// By Sebastian Raaphorst, 2023.

// TODO: https://en.wikipedia.org/wiki/Atkinson_dithering

object Dithering {
    // Apply the Floyd-Steinberg dithering algorithm.
    // We allow this for any set T of values for the Canvas, but in order to perform the computations, we require:
    // 1. A function that maps the Canvas' transformed colours to the closest palette colour, as represented by
    //    a Double. This function should clamp values that are too high to their maximum.
    // 2. An invertible mapping from T to Double as we need a subtraction operator for the dithering.
    // After the dithering is performed, The resultant canvas is translated back from a MutableCanvas<Double> to
    // a Canvas<T> via the invertible mapping.
    fun <T> ditheringFloydSteinbergT(canvas: Canvas<T>,
                                     closestPaletteColour: (Double) -> Double,
                                     invertibleMapping: Mappings.InvertibleMapping<T, Double>): Canvas<T> {

        // Create a mutable copy of the canvas with Double values.
        val mutableCanvas = canvas.apply { invertibleMapping.map(it) }.mutable()

        (0 until mutableCanvas.data.size).forEach { row ->
            (0 until mutableCanvas.data[row].size).forEach { col ->
                val oldPixel = mutableCanvas.data[row][col]
                val newPixel = closestPaletteColour(oldPixel)
                val quantError = oldPixel - newPixel
                if (col + 1 < mutableCanvas.data[row].size)
                    mutableCanvas.data[row][col+1] += quantError *  7.0 / 16
                if (row + 1 < mutableCanvas.data.size) {
                    if (col - 1 >= 0)
                        mutableCanvas.data[row+1][col-1] += quantError * 3.0 / 16
                    mutableCanvas.data[row+1][col] += quantError * 5.0 / 16
                    if (col + 1 < mutableCanvas.data[row].size)
                        mutableCanvas.data[row+1][col+1] += quantError / 16
                }
            }
        }

        return mutableCanvas.apply { invertibleMapping.inverseMap(it) }.immutable()
    }

    // The preferred way to use the Floyd-Steinberg dithering algorithm: receive a Canvas<Double> and
    // perform the operations on it. Afterwards, the user should transform the resultant Canvas<Double> to a
    // Canvas<T> if desired.
    fun ditheringFloydSteinberg(canvas: Canvas<Double>,
                                closestPaletteColour: (Double) -> Double) =
        ditheringFloydSteinbergT(canvas, closestPaletteColour, Mappings.DoubleIdentityMapping)
}