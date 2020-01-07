package com.softwareoverflow.hangtight.ui;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextWatcherMinMax implements TextWatcher {

    private boolean ignore = false; // Indicates if the change came from the TextWatcher

    private EditText editText; // The EditText object the TextWatcher is applied to
    private int minValue, maxValue;

    private int index; // Keeps track of the correct cursor index

    TextWatcherMinMax(EditText editText, int minValue, int maxValue) {
        this.editText = editText;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (count == 0 || count == 1)
            index = start + count;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count == 1)
            index++;
        else if (count == 0)
            index--;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (ignore) return;

        int value = 0;
        try {
            value = Integer.parseInt(s.toString());
        } catch (NumberFormatException ex) {
            // Do nothing, the text field is empty
        }

        // Check min/max values and update the editable to prevent out of bounds exception
        if (value < minValue)
            value = minValue;
        if (value > maxValue)
            value = maxValue;
        s = new SpannableStringBuilder(String.valueOf(value));

        ignore = true;
        editText.setText(String.valueOf(value));
        editText.setSelection(Math.min(Math.max(index, 0), s.length()));

        ignore = false;
    }
}
