package tr.edu.sakarya.analytichierarchyprocess

fun getRandomIndex(n: Int): Float {
    return when (n) {
        1, 2 -> 0f
        3 -> 0.58f
        4 -> 0.9f
        5 -> 1.12f
        6 -> 1.24f
        7 -> 1.32f
        8 -> 1.41f
        9 -> 1.46f
        10 -> 1.49f
        else -> -1f
    }
}

fun multiplyVectors(v1: FloatArray, v2: FloatArray): Float {
    val size = v1.size
    var sum = 0f

    for (i in 0 until size) {
        sum += v1[i] * v2[i]
    }

    return sum
}