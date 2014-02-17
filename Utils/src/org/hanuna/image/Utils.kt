package org.hanuna.image

class Timer() {
    val start = System.currentTimeMillis()
    var startPart: Long = start
    public fun endPart(partName: String = "noname") {
        val end = System.currentTimeMillis()
        println("Part $partName: ${end - startPart} ms")
        startPart = end
    }

    public fun all() {
        val end = System.currentTimeMillis()
        println("All: ${end - start} ms")
        println()
    }
}