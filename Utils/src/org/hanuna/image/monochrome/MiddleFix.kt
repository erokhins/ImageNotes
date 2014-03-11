package org.hanuna.image.monochrome

import org.hanuna.image.*
import java.util.ArrayList
import org.hanuna.image.MutableImage


fun Pixel.isBlack() = r + g + b < 150

fun Image.countInCol(col: Int): Int {
    var sum = 0;
    for (row in 0..this.height) {
        if (this[col, row].isBlack())
            sum++
    }
    return sum;
}

private fun List<Int>.sum(start: Int, end: Int): Int {
    var sum = 0
    for (i in start..end)
        sum += this.get(i)
    return sum
}

class Middle(val middle: Int, val left: Int, val right: Int)

fun Image.getColIndexOfMiddle(): Middle {
    val start = width * 2 / 5 - 10
    val end = width * 3 / 5 + 10
    val countInCols = ArrayList<Int>()
    for (i in start..end){
        countInCols.add(countInCol(i))
    }

    var maxIndex = 0;
    var maxZ = countInCols.sum(0, 10);

    for (i in 1..end-start-13) {
        val curZ = countInCols.sum(i, i + 10)
        if (curZ > maxZ) {
            maxZ = curZ
            maxIndex = i
        }
    }

    val maxGr = maxZ / 10
    var left = maxIndex;
    var right = maxIndex;

    while (left >= 1) {
        if (countInCols.sum(left, left + 10) <= maxGr )
            break
        left--
    }

    while (right <= end-start-13) {
        if (countInCols.sum(right, right + 10) <= maxGr )
            break
        right++
    }

    return Middle(maxIndex + 5 + start, left + 5 + start, right + 5 + start)
}

fun Image.printMiddle(): MutableImage {
    val middle = getColIndexOfMiddle();
    return toNewImage {(original, empty) ->
        original.forAllPixels {
            if (Math.abs(it.col - middle.left) < 4)
                empty[it.col, it.row] = StandardPixels.BLUE
            else if (Math.abs(it.col - middle.middle) < 4)
                empty[it.col, it.row] = StandardPixels.GREEN
            else if (Math.abs(it.col - middle.right) < 4)
                empty[it.col, it.row] = StandardPixels.RED
            else
                empty[it.col, it.row] = original[it.col, it.row]
        }
    }
}

fun Image.needToHide(pixel : PixelWithCoordinates): Boolean {
    if (!pixel.isBlack())
        return false

    var sum = 0
    for (col in -2..1) {
        for (row in -2..1) {
            if (this[pixel.col + col, pixel.row + row].isBlack())
                sum++
        }
    }
    return sum < 4
}

fun Image.fixMiddle(): MutableImage {
    val middle = getColIndexOfMiddle();
    return toNewImage {(original, empty) ->
        original.forAllPixels {
            if (Math.abs(it.col - middle.middle) < 4)
                empty[it.col, it.row] = StandardPixels.GREEN
            else if (!it.isBlack())
                empty[it.col, it.row] = original[it.col, it.row]
            else
                when {
                    it.col < middle.middle -> {
                        if (it.col >= middle.left)
                            empty[it.col, it.row] = StandardPixels.WHITE
                        else if (it.col >= middle.middle - 4 * (middle.middle - middle.left) && needToHide(it))
                            empty[it.col, it.row] = StandardPixels.WHITE
                        else
                            empty[it.col, it.row] = original[it.col, it.row]
                    }

                    it.col > middle.middle -> {
                        if (it.col <= middle.right)
                            empty[it.col, it.row] = StandardPixels.WHITE
                        else if (it.col <= middle.middle + 4 * (middle.right - middle.middle) && needToHide(it))
                            empty[it.col, it.row] = StandardPixels.WHITE
                        else
                            empty[it.col, it.row] = original[it.col, it.row]
                    }
                }
        }
    }
}

fun Image.printStrangePixels(): MutableImage {
    return highlightPixels {
        if (!it.isBlack())
            false
        else if (!this[it.col - 1, it.row].isBlack() && !this[it.col + 1, it.row].isBlack())
            true
        else if (!this[it.col, it.row - 1].isBlack() && !this[it.col, it.row + 1].isBlack())
            true
        else
            false
    }
}
fun Image.highlightPixels(color: Pixel = StandardPixels.RED, highlight: (PixelWithCoordinates) -> Boolean): MutableImage {
    return toNewImage {(original, empty) ->
        original.forAllPixels {
            if (highlight(it)) {
                empty[it.col, it.row] = color
            } else {
                empty[it.col, it.row] = original[it.col, it.row]
            }
        }
    }
}