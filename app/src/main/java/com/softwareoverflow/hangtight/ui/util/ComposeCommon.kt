package com.softwareoverflow.hangtight.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun KeepScreenOn() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = context.findActivity()?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun ScreenTitle(title: String) {
    Text(
        text = title,
        Modifier
            .fillMaxWidth(1f),
        style = typography.h4,
        color = colors.onPrimary,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun ErrorIconWarning(
    message: String,
    modifier: Modifier = Modifier,
) {
    IconTextRow(
        message = message,
        icon = Icons.Filled.Error,
        modifier = modifier,
        iconSize = 24.dp,
        textStyle = typography.caption
    )
}

@Composable
fun IconAboveText(imageVector: ImageVector, text: String, modifier: Modifier = Modifier, color: Color = colors.onSurface) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector, text, tint = color)
        Text(text, style = typography.caption, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun IconTextRow(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconSize: Dp,
    textStyle: TextStyle = TextStyle.Default
) {

    Row(
        modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon, null,
            Modifier
                .size(iconSize)
                .padding(end = 4.dp)
        )
        Text(
            message,
            style = textStyle
        )
    }
}