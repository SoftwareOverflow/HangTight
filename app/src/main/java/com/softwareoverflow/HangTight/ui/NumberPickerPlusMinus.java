package com.softwareoverflow.HangTight.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.softwareoverflow.HangTight.R;

public class NumberPickerPlusMinus extends ConstraintLayout implements View.OnClickListener {

    private EditText editText;

    public NumberPickerPlusMinus(Context context) {
        super(context);
    }

    public NumberPickerPlusMinus(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public NumberPickerPlusMinus(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs) {
        View view = inflate(getContext(), R.layout.partial_create_workout_number_picker, this);

        // Load the array values
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.NumberPickerPlusMinus);
        String name = arr.getString(R.styleable.NumberPickerPlusMinus_np_name);
        int defaultValue = arr.getInt(R.styleable.NumberPickerPlusMinus_np_defaultValue, 5);
        int maxValue = arr.getInt(R.styleable.NumberPickerPlusMinus_np_maxValue, 999);
        arr.recycle();

        //Set up all the UI side
        ((TextView) view.findViewById(R.id.pickerName)).setText(name);

        editText = view.findViewById(R.id.editText_numberPicker);
        editText.setText(String.valueOf(defaultValue));
        editText.addTextChangedListener(new TextWatcherMinMax(editText, 0, maxValue));

        view.findViewById(R.id.plusButton).setOnClickListener(this);
        view.findViewById(R.id.minusButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int delta = 0;

        switch (v.getId()) {
            case R.id.plusButton:
                delta = 1;
                break;
            case R.id.minusButton:
                delta = -1;
                break;
        }

        int newValue = Integer.parseInt(editText.getText().toString()) + delta;
        editText.setText(String.valueOf(newValue));
    }

    public int getValue() {
        return Integer.parseInt(editText.getText().toString());
    }

    public void setValue(int value) {
        editText.setText(String.valueOf(value));
    }
}