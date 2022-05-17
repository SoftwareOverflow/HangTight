package com.softwareoverflow.hangtight.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.ui.theme.AppTheme

abstract class NumberConverter<T : Number> {
    abstract fun canConvertToNumberType(
        value: String,
        allowZero: Boolean = true,
        allowNegatives: Boolean = true
    ): Boolean

    abstract fun convertToNumberType(
        value: String
    ): T
}

class IntNumberConverter : NumberConverter<Int>() {
    override fun canConvertToNumberType(
        value: String,
        allowZero: Boolean,
        allowNegatives: Boolean
    ): Boolean {
        try {
            val numValue = value.toInt()

            if (numValue == 0 && !allowZero) {
                return false
            }

            if (numValue < 0 && !allowNegatives)
                return false

            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }

    override fun convertToNumberType(value: String): Int {
        return value.toInt()
    }
}

@Composable
fun <T : Number> NumberFieldPlusMinus(
    value: T,
    numberConverter: NumberConverter<T>,
    label: String,
    modifier: Modifier = Modifier,
    allowZero: Boolean = false,
    allowNegatives: Boolean = false,
    maxLength: Int = 3,
    onInputError: (() -> Unit)? = null,
    onValueChanged: (T) -> Unit,
) {

    var input by remember {
        mutableStateOf(
            TextFieldValue(
                text = value.toString(),
                selection = TextRange(value.toString().length)
            )
        )
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            "$label:",
            style = typography.h5,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(3f)
        )

        val focusManager = LocalFocusManager.current

        TextField(
            value = input,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(7f),
            isError = !numberConverter.canConvertToNumberType(
                input.text,
                allowZero,
                allowNegatives
            ),
            leadingIcon = {
                Icon(
                    Icons.Filled.ArrowUpward,
                    stringResource(R.string.content_desc_increment),
                    Modifier
                        .clickable {
                            val current = input.text.toIntOrNull()
                            if (current != null) {
                                var newVal = (current + 1)

                                if(newVal < 0 && !allowNegatives)
                                    newVal = 0

                                if(newVal == 0 && !allowZero)
                                    newVal = 1

                                if (newVal.toString().length <= maxLength) {
                                    input = TextFieldValue(text = newVal.toString())
                                    onValueChanged(numberConverter.convertToNumberType(input.text))
                                }
                            }
                        },
                    tint = colors.primary
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Filled.ArrowDownward,
                    stringResource(R.string.content_desc_decrement),
                    Modifier
                        .clickable {
                            val current = input.text.toIntOrNull()
                            if (current != null) {
                                val newVal = current - 1

                                if (newVal > 0 || (newVal == 0 && allowZero)) {
                                    input = TextFieldValue(text = newVal.toString())
                                    onValueChanged(numberConverter.convertToNumberType(input.text))
                                }
                            }
                        },
                    tint = colors.primary
                )
            },
            onValueChange = { textFieldValue ->

                val newString = textFieldValue.text
                val text = if (newString.length > maxLength)
                    newString.substring(0 until maxLength)
                else
                    newString

                input = textFieldValue.copy(text = text)

                if (numberConverter.canConvertToNumberType(input.text, allowZero, allowNegatives))
                    onValueChanged(numberConverter.convertToNumberType(input.text))
                else
                    if (onInputError != null)
                        onInputError()
            },
            textStyle = typography.h4.copy(textAlign = TextAlign.Center),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
        )
    }
}

@Preview
@Composable
private fun Preview_NumberFieldPlusMinus() {
    AppTheme {
        NumberFieldPlusMinus(
            value = 17,
            numberConverter = IntNumberConverter(),
            label = "Recover",
            onValueChanged = {},
            modifier = Modifier.height(100.dp)
        )
    }
}