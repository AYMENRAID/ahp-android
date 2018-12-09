package com.emirhanaydin.analytichierarchyprocess

data class Criterion(
    val name: String,
    var rating: Int = 1
) {
    var alternativesList: ArrayList<Alternatives>? = null
}