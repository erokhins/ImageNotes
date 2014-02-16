package org.hanuna.image

import javax.imageio.ImageIO
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.stream.FileImageOutputStream
import javax.imageio.ImageWriteParam
import java.awt.image.BufferedImage

fun readImageFile(filename: String): MutableImage {
    val bufferedImage = ImageIO.read(File(filename))!!
    return bufferedImage.toMutableImage()
}

fun Image.writeImageToFile(filename: String, quality : Float = 0.9f) {
    writeJpeg(toBufferedImage(), filename, quality)
}

private fun writeJpeg(image : BufferedImage, destFile : String, quality : Float) {
    val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
    val param = writer.getDefaultWriteParam()
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
    param.setCompressionQuality(quality)

    val output = FileImageOutputStream(File(destFile))
    writer.setOutput(output)
    val iioImage = IIOImage(image, null, null)
    writer.write(null, iioImage, param)
}