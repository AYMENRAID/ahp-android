package tr.edu.sakarya.analytichierarchyprocess

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax(private val min: Int, private val max: Int) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val input = (dest.toString() + source.toString()).toIntOrNull()
        return if (input in min..max) null else ""
    }
}
