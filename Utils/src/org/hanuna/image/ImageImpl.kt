package org.hanuna.image

import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

fun BufferedImage.toMutableImage(): MutableImage = PixelArrayAsImage(this)

fun Byte.to255Int(): Int = this.toInt() and 0xff

class PixelArrayAsImage(bufferedImage: BufferedImage): MutableImage {
    private val hasAlphaChannel = bufferedImage.getAlphaRaster() != null
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
        val startIndex = startIndex(col, row)
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
        val startIndex = startIndex(col, row)
        pixels[startIndex] = value.b.toByte()
        pixels[startIndex + 1] = value.g.toByte()
        pixels[startIndex + 2] = value.r.toByte()

        if (hasAlphaChannel)
            pixels[startIndex + 3] = value.alpha.toByte()
    }

    override fun toBufferedImage(): BufferedImage {
        val imgType = if (hasAlphaChannel) BufferedImage.TYPE_4BYTE_ABGR else BufferedImage.TYPE_3BYTE_BGR
        val image = BufferedImage(width, height, imgType)
        val imagePixels = (image.getRaster()!!.getDataBuffer() as DataBufferByte).getData()!!;
        System.arraycopy(pixels, 0, imagePixels, 0, pixels.size)
        return image
    }

}

