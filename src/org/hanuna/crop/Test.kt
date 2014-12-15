package org.hanuna.crop

/**
 * Created by smevok on 5/9/14.
 */

import org.hanuna.image.*
import org.hanuna.image.crop.*
import org.hanuna.image.monochrome.toMonochrome

fun main(args: Array<String>) {
//    for(i in 69..76)
        run("1.jpg")
}

val dir = "/home/smevok/"

fun run(img: String) {
    val image = readImageFile("$dir/${img}")
    val markImg = image.toMonochrome()
    markImg.writeImageToJpg("$dir/out/$img")
}