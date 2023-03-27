// By Sebastian Raaphorst, 2023.

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

internal typealias ImageChunk<T> = List<List<T>>

internal data class MutableCanvas<T>(val data: List<MutableList<T>>) {
    fun immutable(): Canvas<T> =
        Canvas(data.map { row -> row.map { it } })

    fun <S> apply(mapping: (T) -> S): MutableCanvas<S> =
        MutableCanvas(data.map { row -> row.map(mapping).toMutableList() })
}

data class Canvas<T>(val data: List<List<T>>) {
    internal fun mutable(): MutableCanvas<T> =
        MutableCanvas(data.map { row -> row.map { it }.toMutableList() })

    fun <S> apply(mapping: (T) -> S): Canvas<S> =
        Canvas(data.map { row -> row.map(mapping) })

    private fun subgrids(chunkHeight: Int, chunkWidth: Int): List<List<List<List<T>>>> {
        return data
            .map { it.windowed(chunkHeight, chunkHeight) }
            .flatMap { it.withIndex() }
            .groupBy { it.index }
            .map { row -> row.value.map { it.value }.chunked(chunkWidth) }
    }

    // This can be used to reduce the image to a smaller size by breaking the image into n x m pieces and
    // combining them via the specified function.
    // Note that the chunkHeight and chunkWidth do not have to divide the canvas size evenly: if the
    // number of rows or columns are not divisible by the chunkHeight and chunkWidth respectively, then
    // the final chunks at the end of columns or rows will simply be smaller, but still contain a
    // List of Lists of equal length.
    // It is probably better to use the more sophisticated fromPNGScaled and employ the Java algorithms to
    // scale an image that to use this reducer.
    fun <S> reduce(chunkHeight: Int, chunkWidth: Int, reducer: (ImageChunk<T>) -> S): Canvas<S> {
        val chunks = subgrids(chunkHeight, chunkWidth)
        val reduced = chunks.map { rowChunks ->
            rowChunks.map { chunk -> reducer(chunk)}
        }
        return Canvas(reduced)
    }

    companion object {
        // Read an image in from a PNG, and if scale is > 1, scales is down by that factor.
        fun fromPNG(file: File): Canvas<Int> {
            val bufferedImage = ImageIO.read(file)
            val width = bufferedImage.width
            val height = bufferedImage.height
            val pixels = IntArray(width * height)
            bufferedImage.getRGB(0, 0, width, height, pixels, 0, width)
            return Canvas(pixels.toList().chunked(width))
        }

        fun fromPNGScaled(file: File, scale: Int = 1, write: Boolean = false): Canvas<Int> {
            val bufferedImage = ImageIO.read(file)

            val scaledWidth = bufferedImage.width / scale
            val scaledHeight = bufferedImage.height / scale
            val scaledImage = BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB)
            val g2d = scaledImage.createGraphics()
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
            g2d.drawImage(bufferedImage, 0, 0, scaledWidth, scaledHeight, null)
            if (write)
                ImageIO.write(scaledImage, "PNG", File(file.parent, "out.png"))

            val pixels = IntArray(scaledWidth * scaledHeight)
            scaledImage.getRGB(0, 0, scaledWidth, scaledHeight, pixels, 0, scaledWidth)
            return Canvas(pixels.toList().chunked(scaledWidth))
        }
    }
}
