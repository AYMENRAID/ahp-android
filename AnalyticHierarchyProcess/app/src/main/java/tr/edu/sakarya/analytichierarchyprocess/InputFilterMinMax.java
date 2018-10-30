package tr.edu.sakarya.analytichierarchyprocess;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private int min;
    private int max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int input = Integer.parseInt(dest.toString() + source.toString());
        if (input >= min && input <= max)
            return null;

        return "";
    }
}
