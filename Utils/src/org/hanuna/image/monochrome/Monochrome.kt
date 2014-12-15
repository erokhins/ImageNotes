package org.hanuna.image.monochrome

import org.hanuna.image.*
import java.util.ArrayList


fun Image.getBlockSize(): Int = 30

fun Pixel.sq(): Int = this.r

fun Image.toMonochrome(): MutableImage {
    val blockSize = getBlockSize()
    val pixelsInBlock = blockSize * blockSize
    return toNewImage {(original, empty) ->
        forAllBlock(blockSize) {
            var sum: Int = 0
            action {  (col, row) ->
                sum += original[col, row].sq()
            }
            val bound1 = Math.round((sum / pixelsInBlock) * 0.87f)
            val bound2 = Math.round((sum / pixelsInBlock) * 0.85f)
            action { (col, row) ->
                empty[col, row] = if (original[col, row].sq() > bound1)
                    StandardPixels.WHITE
                else if (original[col, row].sq() > bound2)
                    StandardPixels.GRAY
                else
                    StandardPixels.BLACK
            }
        }
    }
}

class ActionForPartImage(val colRange: IntRange, val rowRange: IntRange) {

    fun action(action: (col: Int, row: Int) -> Unit) {
        for (col in colRange) for (row in rowRange) {
            action(col, row)
        }
    }
}

fun Image.forAllBlock(blockSize: Int, allActions: ActionForPartImage.() -> Unit) {
    val blockColCount = (width - 1) / blockSize
    val blockRowCount = (height - 1) / blockSize
    for(bigCol in 0..blockColCount) for(bigRow in 0..blockRowCount) {
        val colRange = bigCol*blockSize..bigCol*blockSize + blockSize - 1
        val rowRange = bigRow*blockSize..bigRow*blockSize + blockSize - 1
        ActionForPartImage(colRange, rowRange).allActions()
    }
}