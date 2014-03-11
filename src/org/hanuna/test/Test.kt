package org.hanuna.test

import org.hanuna.image.*
import org.hanuna.image.monochrome.toMonochrome
import org.hanuna.image.monochrome.colorer
import org.hanuna.image.monochrome.printMiddle
import org.hanuna.image.monochrome.printStrangePixels
import org.hanuna.image.monochrome.fixMiddle

fun main(args: Array<String>) {
    val timer = Timer()

    for (img in 1..195) {
        var name = "$img"
        if (img < 10)
            name = "0$name"
        if (img < 100)
            name = "0$name"
        runPNG("$name")
    }

    timer.all()
}

fun runPNG(img: String){
    println("Start Image: $img.")
    val timer = Timer()

    var image = readImageFile("img/png/book/${img}.png")
    timer.endPart("read image")

    image = image.fixMiddle()
    timer.endPart("print Middle")

    image.writeImageToPng("img/png/book/${img}_out.png")
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