package org.hanuna.test

import org.hanuna.image.*
import org.hanuna.image.monochrome.toMonochrome
import org.hanuna.image.monochrome.colorer

fun main(args: Array<String>) {
    val timer = Timer()

    for (img in 1..5) {
        runPNG("$img")
    }

    timer.all()
}

fun runPNG(img: String){
    println("Start Image: $img.")
    val timer = Timer()

    val image = readImageFile("img/png/${img}.png")
    timer.endPart("read image")

    image.writeImageToJpg("img/png/${img}_out.jpg")
    timer.endPart("write to file")

    timer.all()
}

fun run(img: String) {
    println("Start Image: $img.")
    val timer = Timer()

    val image = readImageFile("img/${img}.jpg")
    timer.endPart("read image")

    val monochrome = image.toMonochrome()
    timer.endPart("toMonochrome")

    monochrome.writeImageToJpg("img/${img}_out.jpg", 0.99f)
    timer.endPart("write to file")

    timer.all()
}