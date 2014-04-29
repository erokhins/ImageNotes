package org.hanuna.detect

import org.hanuna.image.*

/**
 * @author erokhins
 */

trait ImageWithNoise {
    val image: Image
    val noise: NoiseImage
}

trait ImageGetter<T> {
    val counts: Int
    fun get(index: Int): T

    val width: Int
    val height: Int

    val cols: Int
        get() = width

    val rows: Int
        get() = height
}

trait FloatMask {
    val cols: Int
    val rows: Int
    fun get(col: Int, row: Int): Float
}

trait MutableFloatMask : FloatMask {
    fun set(col: Int, row: Int, value: Float)
}

enum class ImageChannel {
    r
    g
    b
}

fun Image.asFloatMask(channel: ImageChannel = ImageChannel.r): FloatMask = object : FloatMask {
    override val cols: Int = width
    override val rows: Int = height
    override fun get(col: Int, row: Int): Float {
        val fCol = fixCol(col)
        val fRow = fixRow(row)
        return when (channel) {
            ImageChannel.r -> this@asFloatMask[fCol, fRow].r.toFloat()
            ImageChannel.g -> this@asFloatMask[fCol, fRow].g.toFloat()
            ImageChannel.b -> this@asFloatMask[fCol, fRow].b.toFloat()
        }
    }
}

private fun FloatMask.fixCol(col: Int): Int {
    return if (col < 0){
        - col
    } else if (col >= cols) {
        2 * cols - col - 1
    } else {
        col
    }
}

private fun FloatMask.fixRow(row: Int): Int {
    return if (row < 0){
        -row
    } else if (row >= rows) {
        2 * rows - row - 1
    } else {
        row
    }
}

class MutableFloatMaskImpl(override val cols: Int, override val rows: Int) : MutableFloatMask {
    private val values = FloatArray(cols*rows);

    private fun toIndex(col: Int, row: Int) = fixCol(col) * rows + fixRow(row)

    override fun get(col: Int, row: Int): Float = values[toIndex(col, row)]

    override fun set(col: Int, row: Int, value: Float) {
        values[toIndex(col, row)] = value;
    }
}

class Coordinates(val col: Int, val row: Int)

fun Image.get(coord: Coordinates) = get(coord.col, coord.row)
fun MutableImage.set(coord: Coordinates, value: Pixel) = set(coord.col, coord.row, value)

fun FloatMask.forAll(operation: (Coordinates) -> Unit) {
    for (row in 0..rows - 1) {
        for (col in 0..cols - 1) {
            operation(Coordinates(col, row))
        }
    }
}

fun FloatMask.get(coord: Coordinates) = get(coord.col, coord.row)
fun MutableFloatMask.set(coord: Coordinates, value: Float) = set(coord.col, coord.row, value)

fun FloatMask.newEmptyFloatMask(): MutableFloatMask = MutableFloatMaskImpl(cols, rows)

fun FloatMask.plus(mask: FloatMask): FloatMask {
    return object : FloatMask {
        override val cols: Int = this@plus.cols
        override val rows: Int = this@plus.rows
        override fun get(col: Int, row: Int): Float = this@plus[col, row] + mask[col, row]
    }
}

fun FloatMask.times(mask: FloatMask): FloatMask {
    return object : FloatMask {
        override val cols: Int = this@times.cols
        override val rows: Int = this@times.rows
        override fun get(col: Int, row: Int): Float = this@times[col, row] * mask[col, row]
    }
}

fun MutableFloatMask.add(mask: FloatMask) {
    forAll {
        this[it] = this[it] + mask[it]
    }
}

fun FloatMask.subMask(c0: Coordinates, c1: Coordinates) = subMask(c0.col, c0.row, c1.col, c1.row)

// 0,0, 1, 1 - 1*1
fun FloatMask.subMask(col0: Int, row0: Int, col1: Int, row1: Int): FloatMask = object : FloatMask {
    override val cols: Int = col1 - col0
    override val rows: Int = row1 - row0
    override fun get(col: Int, row: Int): Float {
        val fCol = fixCol(col)
        val fRow = fixRow(row)
        return this@subMask[fCol + col0, fRow + row0]
    }
}

fun FloatMask.sum(): Float {
    var sum = 0f;
    forAll {
        sum += this[it]
    }
    return sum
}

val FloatMask.size: Int
    get() = cols * rows

fun Float.square(): Float = this * this

fun FloatMask.calcCov(mask: FloatMask): Float {
    val thisM = sum() / size
    val maskM = mask.sum() / mask.size
    var num = 0f
    var kwT = 0f
    var kwM = 0f

    forAll {
      num += (this[it] - thisM) * (mask[it] - maskM)
      kwT += (this[it] - thisM).square()
      kwM += (mask[it] - maskM).square()
    }

    return num / Math.sqrt((kwT * kwM).toDouble()).toFloat()
}