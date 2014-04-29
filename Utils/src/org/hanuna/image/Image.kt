package org.hanuna.image

import java.awt.image.BufferedImage
import org.opencv.core.Mat
import org.opencv.core.CvType


// in real range of all fields is 0-255
trait Pixel {
    val r: Int
    val g: Int
    val b: Int
    val alpha: Int
}

fun Pixel.equals(other : Pixel) = other.r == this.r && other.g == this.g && other.b == this.b // alpha

trait Image {
    val width: Int  // count of columns
    val height: Int // count of rows

    fun get(col: Int, row: Int): Pixel

    fun toBufferedImage(): BufferedImage

    fun toMat() : MyMat
}

trait MutableImage : Image {
    fun set(col: Int, row: Int, value: Pixel)
}

abstract class MyMat(height: Int, width: Int) : Mat(height, width, CvType.CV_8UC3) {
    abstract fun flush()
}

class PixelWithCoordinates(pixel: Pixel, val col: Int, val row: Int) : Pixel by pixel

fun Image.forAllPixels(operation: (PixelWithCoordinates) -> Unit) {
    for (row in 0..height - 1) {
        for (col in 0..width - 1) {
            operation(PixelWithCoordinates(this[col, row], col, row))
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
    val YELLOW = toPixel(r = 255, g = 255)
}

fun randByte(): Int = Math.round(Math.random() * 255).toInt();
fun randomPixel(): Pixel = toPixel(randByte(), randByte(), randByte())


fun Pixel.onlyRed(): Pixel = object: Pixel {
    override val r: Int = this@onlyRed.r
    override val g: Int = 0
    override val b: Int = 0
    override val alpha: Int = 255
}