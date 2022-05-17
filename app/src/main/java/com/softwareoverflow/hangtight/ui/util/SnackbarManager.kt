package com.softwareoverflow.hangtight.ui.util

import androidx.compose.material.SnackbarDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

data class SnackbarMessage(
    val id: Long,
    val text: String,
    val duration: SnackbarDuration = SnackbarDuration.Long,
    val actionText: String? = null,
    val onAction: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
)

/**
 * Class responsible for managing Snackbar messages to show on the screen
 */
object SnackbarManager {

    private val _messages: MutableStateFlow<List<SnackbarMessage>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<SnackbarMessage>> get() = _messages.asStateFlow()

    fun showMessage(
        text: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
        actionText: String? = null,
        onAction: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ) {
        if(_messages.value.none { it.text == text }){
            _messages.update { currentMessages ->
                currentMessages + SnackbarMessage(
                    id = UUID.randomUUID().mostSignificantBits,
                    text = text,
                    duration = duration,
                    actionText = actionText,
                    onAction = onAction,
                    onDismiss = onDismiss
                )
            }
        }
    }

    fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}

/*
@Composable
fun DefaultSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = { }
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                modifier = Modifier.padding(16.dp).zIndex(Float.MAX_VALUE), // Make sure snackbar is always on top
                content = {
                    Text(
                        text = data.message,
                        style = MaterialTheme.typography.body2
                    )
                },
                action = {
                    data.actionLabel?.let { actionLabel ->
                        TextButton(onClick = { data.performAction() }) {
                            Text(
                                text = actionLabel,
                                color= MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                },
                elevation = 32.dp
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Bottom)
            .zIndex(Float.MAX_VALUE)
    )
}*/
