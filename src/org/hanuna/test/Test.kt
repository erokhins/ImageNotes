package org.hanuna.test

import org.hanuna.image.*
import org.hanuna.image.monochrome.toMonochrome
import org.hanuna.image.monochrome.colorer
import org.hanuna.image.monochrome.printMiddle
import org.hanuna.image.monochrome.printStrangePixels
import org.hanuna.image.monochrome.fixMiddle
import org.hanuna.image.monochrome.getPages

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
    val dir = "book"

    var image = readImageFile("$dir/${img}.png")
    timer.endPart("read image")

    image = image.fixMiddle()

    image.writeImageToPng("$dir/${img}_gr.png")

    val pages = image.getPages();

    pages.first.writeImageToPng("$dir/${img}_p1.png")
    pages.second.writeImageToPng("$dir/${img}_p2.png")
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