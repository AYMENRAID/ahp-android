package com.emirhanaydin.analytichierarchyprocess

@Suppress("UNCHECKED_CAST")
class Criteria(
    parentName: String,
    children: MutableList<Criterion> = mutableListOf(),
    var alternativesList: ArrayList<Alternatives>? = null
) : AhpGroup(parentName, children as MutableList<AhpItem>)