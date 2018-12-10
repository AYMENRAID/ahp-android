package com.emirhanaydin.analytichierarchyprocess

abstract class AhpItem(
    val name: String,
    var rating: Int = 1,
    var isReciprocal: Boolean = false
)