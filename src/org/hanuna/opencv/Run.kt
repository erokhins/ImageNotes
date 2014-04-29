package org.hanuna.opencv

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.CvType
import org.hanuna.image.readImageFile
import org.hanuna.image.Timer
import org.hanuna.image.writeImageToJpg
import org.hanuna.image.fastNlMeansDenoising
import org.hanuna.detect.subtract
import org.hanuna.detect.calculateNoise
import org.hanuna.detect.ImageGetter
import org.hanuna.detect.ImageWithNoise
import org.hanuna.image.Image
import org.hanuna.detect.NoiseImage
import org.hanuna.image.MutableImage
import org.hanuna.image.monochrome.crop
import org.hanuna.detect.calculateMask
import org.hanuna.detect.covPix

/**
 * @author erokhins
 */
val o = System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

val path = "img_cv/"
val WIDTH = 1000
val HEIGHT = 1000


fun saveNoise(img: String, inPath: String, outPath: String) {
    println("Start Image: $img.")
    val timer = Timer()

    val image = readImageFile("$inPath/${img}.jpg")
    timer.endPart("read image")

    val noise = image.calculateNoise()
    timer.endPart("calculateNoise")

    noise.writeImageToJpg("$outPath/${img}_noise.jpg", 0.99f)
    timer.endPart("write to file")

    timer.all()
}

fun img(index: Int): String = if (index > 9) "$index" else "0$index"

fun loadCropImage(filename: String): MutableImage = readImageFile(filename).crop(0, 0, WIDTH, HEIGHT)

fun createNoiseGetter(range: IntRange, imgPath: String, noisePath: String):
        ImageGetter<ImageWithNoise> = object : ImageGetter<ImageWithNoise> {
    override val counts: Int = range.end - range.start + 1
    override fun get(index: Int): ImageWithNoise {
        val imgName = img(index + range.start)
        println("load image: $imgName")
        return object : ImageWithNoise {
            override val image: Image = loadCropImage("$imgPath/$imgName.jpg")
            override val noise: NoiseImage = NoiseImage(loadCropImage("$noisePath/${imgName}_noise.jpg"))
        }
    }
    override val width: Int = WIDTH
    override val height: Int = HEIGHT
}


fun main(args: Array<String>) {
    val getter = createNoiseGetter(0..5, "$path/ok", "$path/ok/noise")
    val im = "00"

    val image = loadCropImage("$path/test/${im}.jpg")
    val noise = loadCropImage("$path/test/${im}_noise.jpg")
    val imWithNoise = object : ImageWithNoise {
        override val image: Image = image
        override val noise: NoiseImage = NoiseImage(noise)
    }

    val mask = calculateMask(getter)
    val result = imWithNoise.covPix(mask)
    result.writeImageToJpg("$path/test/${im}_out.jpg")
}

