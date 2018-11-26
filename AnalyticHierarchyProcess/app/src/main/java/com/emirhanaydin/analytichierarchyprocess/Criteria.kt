package com.emirhanaydin.analytichierarchyprocess

data class Criteria(
    val parent: String,
    val children: MutableList<Criterion>
)