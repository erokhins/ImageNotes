package org.hanuna.image.monochrome

import org.hanuna.image.*


fun Pixel.isGreen() = g > 200 && r < 100 && b < 100

fun Image.getSaveMiddle(): Int {
    for (col in 0..width) {
        if (this[col, 0].isGreen())
            return col
    }
    throw IllegalStateException("");
}

fun Image.crop(col0: Int, row0: Int, col1: Int, row1: Int): MutableImage {
    val empty = newEmptyImage(false, col1 - col0, row1 - row0)
    empty.forAllPixels {
        empty[it.col, it.row] = this[it.col + col0, it.row + row0]
    }
    return empty
}

val MARGIN = 80;

fun Image.getPages(): Pair<MutableImage, MutableImage> {
    val middle = getSaveMiddle()
    val im1 = crop(50, MARGIN, middle - 2, height - MARGIN)
    val im2 = crop(middle + 7, MARGIN, width - 130, height - MARGIN)
    return Pair(im1, im2);
}