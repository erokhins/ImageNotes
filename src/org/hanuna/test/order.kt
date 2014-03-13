package org.hanuna.order

import java.io.File
import java.nio.file.Files

// count of pages/4
val N = 10;

val dir = "book/toPrint/1"

val firstOut = "$dir/first"
val secondOut = "$dir/second"

fun createDirIsNeed(dirName: String) {
    val dir = File(dirName)
    if (!dir.exists())
        dir.mkdir()
}

fun copyFile(inName: String, outName: String) {
    println("$inName -> $outName")
    Files.copy(File(inName).toPath(), File(outName).toPath())
}

fun getCorrectIN(index: Int): String {
    if (index < 10)
        return "0$index"
    else
        return "$index"
}

fun getImageName(dirName: String, imageIndex: Int): String {
    return "$dirName/${getCorrectIN(imageIndex)}.png";
}

fun getFirst(): IntArray {
    val ar = IntArray(2 * N)
    for (i in 0..N-1) {
        ar[2*i] = 2*N - 2 * i
        ar[2*i + 1] = 2*N + 2 * i + 1
    }
    return ar
}

fun getSecond(): IntArray {
    val ar = IntArray(2 * N)
    for (i in 0..N-1) {
        ar[2*i] = 4*N - 2 * i
        ar[2*i + 1] = 2 * i + 1
    }
    return ar
}


fun main(args: Array<String>) {
    val first = getFirst()
    val second = getSecond()

    println(first.toList())
    println(second.toList())


    if (true) {
        createDirIsNeed(firstOut)
        createDirIsNeed(secondOut)
        for(i in 1..2*N) {
            val outIndex = i
            val sourceIndex = first[i - 1]
            copyFile(getImageName(dir, sourceIndex), getImageName(firstOut, outIndex))
        }
        for(i in 1..2*N) {
            val outIndex = i
            val sourceIndex = second[i - 1]
            copyFile(getImageName(dir, sourceIndex), getImageName(secondOut, outIndex))
        }
    }
}