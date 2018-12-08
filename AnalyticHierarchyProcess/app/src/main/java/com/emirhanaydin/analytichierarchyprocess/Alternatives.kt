package com.emirhanaydin.analytichierarchyprocess

data class Alternatives(
    val parent: String,
    val children: MutableList<Alternative>
)