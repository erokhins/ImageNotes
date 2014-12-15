package org.hanuna.image.crop

import org.hanuna.image.*
/**
 * Created by smevok on 5/9/14.
 */

fun Pixel.dist(p: Pixel) = Math.abs(this.r - p.r) + Math.abs(this.g - p.g) + Math.abs(this.b - p.b)

fun Image.average(col: Int, row: Int, radius: Int = 2): Int {
    var b = 0
    for (co in col-radius..col+radius) {
        for (ro in row-radius..row+radius){
            b += this[col, row].b
        }
    }
    val count = (2*radius + 1) * (2*radius + 1)
    return b / count
}

fun Image.isBigDelta(p: PixelWithCoordinates): Boolean {
    val r = 3
    val contr = average(p.col - r, p.row, r) + average(p.col, p.row - r, r) - 2 * average(p.col, p.row, r)
    return contr > 5
}


fun Image.markBigDelta(): MutableImage {
    val im = newEmptyImage()
    var r = 0;
    forAllPixels {
        if (r != it.row) {
            println(it.row)
            r = it.row
        }
        if (isBigDelta(it))
            im[it.col, it.row] = StandardPixels.RED
        else
            im[it.col, it.row] = it
    }
    return im
}
