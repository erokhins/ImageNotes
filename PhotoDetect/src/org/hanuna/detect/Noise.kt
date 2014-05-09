package org.hanuna.detect

import org.hanuna.image.*

/**
 * @author erokhins
 */

fun Pixel.minus(pixel: Pixel): Pixel {
    return object : Pixel {
        override val r: Int = this@minus.r - pixel.r
        override val g: Int = this@minus.g - pixel.g
        override val b: Int = this@minus.b - pixel.b
        override val alpha: Int = 0
    }
}

fun MutableImage.subtract(image: Image) {
    forAllPixels {
        this[it.col, it.row] = it - image[it.col, it.row]
    }
}

fun Image.calculateNoise(): NoiseImage {
    val filterer = fastNlMeansDenoising()
    filterer.subtract(this)
    return NoiseImage(filterer)
}

class NoiseImage(val delegate: MutableImage) : MutableImage by delegate {
    fun toNoiseInt(value: Int): Int {
        if (value > 100)
            return value - 256;
        else
            return value;
    }
    override fun get(col: Int, row: Int): Pixel {
        val pixel = delegate[col, row]
        return object : Pixel {
            override val r: Int = toNoiseInt(pixel.r)
            override val g: Int = toNoiseInt(pixel.g)
            override val b: Int = toNoiseInt(pixel.b)
            override val alpha: Int = 0
        }
    }
}

fun calculateMask(images: ImageGetter<ImageWithNoise>,
                  channel: ImageChannel = ImageChannel.r): MutableFloatMask {
    val maskWI = MutableFloatMaskImpl(images.cols, images.rows)
    val maskII = MutableFloatMaskImpl(images.cols, images.rows)
    for (i in 0..images.counts-1) {
        val mI = images.get(i).image.asFloatMask(channel)
        val mW = images.get(i).noise.asFloatMask(channel)

        maskII.add(mI * mI)
        maskWI.add(mI * mW)
    }

    val mask = MutableFloatMaskImpl(images.cols, images.rows)
    mask.forAll {
       mask[it] = maskWI[it] / maskII[it]
    }

    return mask
}

fun Coordinates.minus(r: Int): Coordinates = Coordinates(col - r, row - r)
fun Coordinates.plus(r: Int): Coordinates = Coordinates(col + r, row + r)

fun ImageWithNoise.covPix(mask: FloatMask, channel: ImageChannel = ImageChannel.r): MutableImage {
    val out = image.newEmptyImage()
    val imageMask = image.asFloatMask(channel)

    val mainMask = mask * imageMask
    val noiseMask = noise.asFloatMask(channel)
    val covEvaluator = CovEvaluator(mainMask, noiseMask)

    mainMask.forAll {
        val cov = covEvaluator[it]
        out[it] = cov.toPixel()
    }
    return out;
}

fun Float.toPixel(): Pixel {
    val zn = Math.abs(this) * 25 * 255
    if (zn > 255) {
        if (this > 0)
            return StandardPixels.BLUE
        else
            return StandardPixels.YELLOW
    }
    if (this > 0)
        return toPixel(r = zn.toInt())
    else
        return toPixel(g = zn.toInt())
}

fun FloatMask.times(value: Float) = object: FloatMask {
    override val cols: Int = this@times.cols
    override val rows: Int = this@times.rows
    override fun get(col: Int, row: Int): Float = this@times[col, row] * value

}