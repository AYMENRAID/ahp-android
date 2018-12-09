package com.emirhanaydin.analytichierarchyprocess

data class Criteria(
    val parentName: String,
    val children: MutableList<Criterion> = mutableListOf(),
    var alternativesList: ArrayList<Alternatives>? = null
)