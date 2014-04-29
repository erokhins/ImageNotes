package org.hanuna.detect

/**
 * @author erokhins
 */

fun FloatMask.createSums(): FloatMask {
    val sums = MutableFloatMaskImpl(cols, rows);
    sums[0, 0] = this[0, 0]

    for (col in 1..sums.cols-1)
        sums[col, 0] = sums[col - 1, 0] + this[col, 0]
    for (row in 1..sums.rows-1)
        sums[0, row] = sums[0, row - 1] + this[0, row]

    for (col in 1..sums.cols-1)
        for (row in 1..sums.rows-1) {
            sums[col, row] = sums[col - 1, row] + sums[col, row - 1] + this[col, row] - sums[col - 1, row - 1]
        }

    return sums
}

class SumWrapper(val mask: FloatMask, val R: Int = 100) {
    val wrapper = object: FloatMask {
        override val cols: Int = mask.cols + 2*R
        override val rows: Int = mask.rows + 2*R
        override fun get(col: Int, row: Int): Float = mask[col - R, row - R]
    }
    val sums = wrapper.createSums()

    fun calc(c0: Coordinates, c1: Coordinates): Float
            = sums[c1.col, c1.row] + sums[c0.col, c0.row] - sums[c1.col, c0.row] - sums[c0.col, c1.row]

    // include c0 & c1
    fun get(c0: Coordinates, c1: Coordinates): Float = calc(c0 + R - 1, c1 + R)
}

fun Int.square() = this * this

fun Float.sqrt() = Math.sqrt(this.toDouble()).toFloat()

class CovEvaluator(val maskX: FloatMask, val maskY: FloatMask, val R: Int = 64) {
    val sumX = SumWrapper(maskX, R + 10)
    val sumY = SumWrapper(maskY, R + 10)
    val sumXY = SumWrapper(maskX * maskY, R + 10)
    val sumXX = SumWrapper(maskX * maskX, R + 10)
    val sumYY = SumWrapper(maskY * maskY, R + 10)

    fun get(coord: Coordinates): Float {
        val N = (2*R + 1).square()
        val c0 = coord - R
        val c1 = coord + R
        val num = sumXY[c0, c1] / N - (sumX[c0, c1] / N) * (sumY[c0, c1] / N)
        val tX = sumXX[c0, c1] / N - (sumX[c0, c1] / N).square()
        val tY = sumYY[c0, c1] / N - (sumY[c0, c1] / N).square()
        return num / (tX * tY).sqrt()
    }
}