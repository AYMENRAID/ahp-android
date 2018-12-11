package com.emirhanaydin.analytichierarchyprocess

fun getRandomIndex(n: Int): Float {
    return when (n) {
        1, 2 -> 0f
        3 -> 0.58f
        4 -> 0.90f
        5 -> 1.12f
        6 -> 1.24f
        7 -> 1.32f
        8 -> 1.41f
        9 -> 1.45f
        10 -> 1.49f
        11 -> 1.51f
        12 -> 1.48f
        13 -> 1.56f
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

private fun getWeights(ratings: Array<FloatArray>): Pair<Array<FloatArray>, FloatArray> {
    val size = ratings.size
    val weights = Array(size) { FloatArray(size) }
    val subtotals = FloatArray(size)

    // Calculate the weights
    for (i in 0 until size) {
        val child = ratings[i]

        for (j in 0 until child.size) {
            val index = j + i + 1 // Plus i to make the calculations triangular, plus 1 to skip diagonal

            weights[i][index] = child[j]
            weights[index][i] = 1 / weights[i][index] // Reciprocal

            // Add the values to subtotals by their column indexes
            subtotals[index] += weights[i][index]
            subtotals[i] += weights[index][i]
        }
        // The diagonal indicates the alternative itself, so its weight is 1
        weights[i][i] = 1f
        subtotals[i] += weights[i][i]
    }

    return Pair(weights, subtotals)
}

private fun getNormalizedWeights(weights: Array<FloatArray>, subtotals: FloatArray): Array<FloatArray> {
    val size = weights.size
    val normalizedWeights = Array(size) { FloatArray(size) }

    // Normalize the weights
    for (i in 0 until size) {
        for (j in 0 until size) {
            normalizedWeights[i][j] = weights[i][j] / subtotals[j]
        }
    }

    return normalizedWeights
}

private fun getPriorities(normalizedWeights: Array<FloatArray>): FloatArray {
    val size = normalizedWeights.size
    val priorities = FloatArray(size)

    // Calculate priorities with the normalized weights
    for (i in 0 until size) {
        var sum = 0f
        for (j in 0 until size) {
            sum += normalizedWeights[i][j]
        }
        priorities[i] = sum / size // Average of the row
    }

    return priorities
}

private fun getConsistencyRatio(priorities: FloatArray, subtotals: FloatArray): Pair<Float, Float> {
    val size = priorities.size

    // Calculate the consistency ratio
    val eMax = multiplyVectors(priorities, subtotals)
    val consistencyIndex = (eMax - size) / (size - 1)

    return Pair(consistencyIndex, getRandomIndex(size))
}

fun performAhp(ratings: Array<FloatArray>): AhpResult {
    val weights: Array<FloatArray>
    val subtotals: FloatArray
    getWeights(ratings).apply {
        weights = first
        subtotals = second
    }
    val normalizedWeights = getNormalizedWeights(weights, subtotals)

    val priorities = getPriorities(normalizedWeights)
    val randomIndex: Float
    val consistencyIndex: Float
    getConsistencyRatio(priorities, subtotals).apply {
        consistencyIndex = first
        randomIndex = second
    }
    return AhpResult(priorities, consistencyIndex, randomIndex, consistencyIndex / randomIndex)
}

class AhpResult(
    val priorities: FloatArray,
    val consistencyIndex: Float,
    val randomIndex: Float,
    val consistencyRatio: Float
)