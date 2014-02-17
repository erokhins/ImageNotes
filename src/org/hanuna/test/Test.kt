package org.hanuna.test

import org.hanuna.image.*

fun main(args: Array<String>) {
    val img = "3"
    val timer = Timer()

    val image = readImageFile("img/${img}.jpg").newEmptyImage()
    timer.endPart("read image")

    var c = 0
    image.forAllPixels {
        c++
        toPixel(c % 255)
    }
    timer.endPart("toRed")

    image.writeImageToFile("img/${img}_out.jpg")
    timer.endPart("write to file")

    timer.all()
}