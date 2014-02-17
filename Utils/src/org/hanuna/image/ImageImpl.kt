package org.hanuna.image

import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

fun BufferedImage.toMutableImage(): MutableImage = PixelArrayAsImage(this)

fun Byte.to255Int(): Int = this.toInt() and 0xff

class PixelArrayAsImage(val bufferedImage: BufferedImage): MutableImage {
    val hasAlphaChannel = bufferedImage.getAlphaRaster() != null
    private val pixels = (bufferedImage.getRaster()!!.getDataBuffer() as DataBufferByte).getData()!!;

    override val width: Int = bufferedImage.getWidth()
    override val height: Int = bufferedImage.getHeight()

    fun startIndex(col: Int, row: Int): Int {
        val index = row * width + col
        return if (hasAlphaChannel) {
            4 * index
        } else {
            3 * index
        }
    }


    override fun get(col: Int, row: Int): Pixel {
        val startIndex = startIndex(fixCol(col), fixRow(row))
        val pixels = pixels
        val hasAlphaChannel = hasAlphaChannel
        return object : Pixel {
            override val b: Int = pixels[startIndex].to255Int()
            override val g: Int = pixels[startIndex + 1].to255Int()
            override val r: Int = pixels[startIndex + 2].to255Int()
            override val alpha: Int = if (hasAlphaChannel) pixels[startIndex + 3].to255Int() else 255
        }
    }

    override fun set(col: Int, row: Int, value: Pixel) {
        if (outCol(col) || outRow(row)) return

        val startIndex = startIndex(col, row)
        pixels[startIndex] = value.b.toByte()
        pixels[startIndex + 1] = value.g.toByte()
        pixels[startIndex + 2] = value.r.toByte()

        if (hasAlphaChannel)
            pixels[startIndex + 3] = value.alpha.toByte()
    }

    override fun toBufferedImage(): BufferedImage {
        return bufferedImage
    }

    private fun outCol(col: Int) = (col < 0) || (col >= width)
    private fun outRow(row: Int) = (row < 0) || (row >= height)

    private fun fixCol(col: Int): Int {
        return if (col < 0){
            - col
        } else if (col >= width) {
            2 * width - col - 1
        } else {
            col
        }
    }

    private fun fixRow(row: Int): Int {
        return if (row < 0){
            -row
        } else if (row >= height) {
            2 * height - row - 1
        } else {
            row
        }
    }

}

