package com.softwareoverflow.hangtight.ui.util

import androidx.compose.animation.Animatable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

@Composable
fun DisableableFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small.copy(
        CornerSize(percent = 50)
    ),
    enabled: () -> Boolean,
    backgroundColorEnabled: Color = MaterialTheme.colors.secondary,
    backgroundColorDisabled: Color = Color.LightGray,
    contentColor: Color = contentColorFor(backgroundColorEnabled),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit,
) {

    CompositionLocalProvider(
        LocalRippleTheme provides
                if (enabled())  LocalRippleTheme.current else NoRippleTheme
    ) {
        val color = remember { Animatable(backgroundColorEnabled) }
        LaunchedEffect(enabled()) {
            color.animateTo(if (enabled()) backgroundColorEnabled else backgroundColorDisabled)
        }

        FloatingActionButton(
            onClick = { if(enabled()) onClick()},
            modifier = modifier,
            interactionSource = interactionSource,
            shape = shape,
            backgroundColor = color.value,
            contentColor = contentColor,
            elevation = elevation
        ) {
            content()
        }
    }
}

object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}