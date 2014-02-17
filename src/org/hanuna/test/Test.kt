package org.hanuna.test

import org.hanuna.image.*
import org.hanuna.image.monochrome.toMonochrome

fun main(args: Array<String>) {
    val timer = Timer()

    for (img in 2..9) {
        run("$img")
    }

    timer.all()
}

fun run(img: String) {
    println("Start Image: $img.")
    val timer = Timer()

    val image = readImageFile("img/${img}.jpg")
    timer.endPart("read image")

    val monochrome = image.toMonochrome()
    timer.endPart("toMonochrome")

    monochrome.writeImageToFile("img/${img}_out.jpg", 0.99f)
    timer.endPart("write to file")

    timer.all()
}