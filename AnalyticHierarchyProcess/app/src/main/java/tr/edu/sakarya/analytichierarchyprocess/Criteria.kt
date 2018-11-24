package tr.edu.sakarya.analytichierarchyprocess

data class Criteria(
    val parent: String,
    val children: MutableList<Criterion>
)