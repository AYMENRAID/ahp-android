package com.emirhanaydin.analytichierarchyprocess

abstract class AhpGroup(
    val parentName: String,
    val children: MutableList<AhpItem>
)