package org.hanuna.image.monochrome

import org.hanuna.image.*
import java.util.HashSet

fun Image.colorer(): MutableImage {
    return toNewImage {(original, empty) ->
        original.forAllPixels {
            if (original[it.col, it.row].equals(StandardPixels.WHITE)) {
                empty[it.col, it.row] = StandardPixels.WHITE;
            }
            else if (empty[it.col, it.row].equals(StandardPixels.BLACK)) {
                // new block
                fillBlock(original, empty, it.col, it.row)
            }
//            println("${it.col} ${it.row}")
        }
    }
}


fun Image.counter(col: Int, row: Int, size: Int, counter: (Pixel) -> Int): Int {
    var sum = 0
    for (colI in col-size..col+size) {
        for (rowI in row-size..row+size) {
            sum += counter(this[colI, rowI])
        }
    }

    return sum
}

fun Image.counterBool(col: Int, row: Int, size: Int, counter2: (Pixel) -> Boolean): Int {
    return counter(col, row, size) {
        if (counter2(it)) {
            1
        } else {
            0
        }
    }
}

fun doSmbdForBlock(col: Int, row: Int, size: Int, doSmbd: (Int, Int) -> Unit) {
    for (colI in col-size..col+size) {
        for (rowI in row-size..row+size) {
            doSmbd(colI, rowI)
        }
    }
}

fun fillBlock(original: Image, empty: MutableImage, col: Int, row: Int) {
    val color = randomPixel()
    empty[col, row] = color

    val set = HashSet<Pair<Int, Int>>()
    set.add(Pair(col, row))

    fun addPixelToColor(col: Int, row: Int, bound: Int = 0) {
        if (original[col, row].equals(StandardPixels.WHITE) || !empty[col, row].equals(StandardPixels.BLACK))
            return
        if (col < 0 || col >= empty.width || row < 0 || row >= empty.height)
            return

        val count = empty.counterBool(col, row, 4) {
            it.equals(color)
        }

        if (count > bound) {
            set.add(Pair(col, row))
            empty[col, row] = color
        }
    }

    doSmbdForBlock(col, row, 3) {(col, row) ->
        addPixelToColor(col, row, 0)
    }

    while (!set.isEmpty()) {
        val some = set.first()
        set.remove(some)

        doSmbdForBlock(some.first, some.second, 4) {(col, row) ->
            addPixelToColor(col, row)
        }


    }
}