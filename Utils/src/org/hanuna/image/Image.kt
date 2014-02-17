package org.hanuna.image

import java.awt.image.BufferedImage


// in real range of all fields is 0-255
trait Pixel {
    val r: Int
    val g: Int
    val b: Int
    val alpha: Int
}

trait Image {
    val width: Int  // count of columns
    val height: Int // count of rows

    fun get(col: Int, row: Int): Pixel

    fun toBufferedImage(): BufferedImage
}

trait MutableImage : Image {
    fun set(col: Int, row: Int, value: Pixel)
}

class PixelWithCoordinates(pixel: Pixel, val col: Int, val row: Int) : Pixel by pixel

fun MutableImage.forAllPixels(operation: (PixelWithCoordinates) -> Pixel) {
    for (row in 0..height - 1) {
        for (col in 0..width - 1) {
            this[col, row] = operation(PixelWithCoordinates(this[col, row], col, row))
        }
    }
}

fun Image.toNewImage(draw: (original: Image, empty: MutableImage) -> Unit): MutableImage {
    val newImage = newEmptyImage()
    draw(this, newImage)
    return newImage
}


fun toPixel(r: Int = 0, g: Int = 0, b: Int = 0, alpha: Int = 255): Pixel = object: Pixel {
    override val r: Int = r
    override val g: Int = g
    override val b: Int = b
    override val alpha: Int = alpha
}

object StandardPixels {
    val BLACK = toPixel()
    val GRAY = toPixel(128, 128, 128)
    val WHITE = toPixel(255, 255, 255)
    val RED = toPixel(255)
    val GREEN = toPixel(0, 255)
    val BLUE = toPixel(0, 0, 255)
}


fun Pixel.onlyRed(): Pixel = object: Pixel {
    override val r: Int = this@onlyRed.r
    override val g: Int = 0
    override val b: Int = 0
    override val alpha: Int = 255
}