package com.softwareoverflow.hangtight.logging

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.ui.theme.AppTheme

@Composable
fun ConsentDialog(onAccept: () -> Unit) {
    var openDialog by remember { mutableStateOf(FirebaseManager.showConsentDialog) }

    if (openDialog)
        ConsentDialogContent {
            openDialog = false
            onAccept()
        }
}

@Composable
private fun ConsentDialogContent(onAccept: () -> Unit) {
    Dialog(
        onDismissRequest = { /* Do nothing */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {

        Card {
            Column(
                horizontalAlignment = CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Pardon the interruption...",
                    style = typography.h4,
                    textAlign = TextAlign.Center
                )
                val annotatedLinkString = buildAnnotatedString {
                    val string = LocalContext.current.getString(R.string.user_consent_details)
                    val textToLink = "privacy policy"

                    val linkStartIndex = string.indexOf(textToLink)
                    val linkEndIndex = linkStartIndex + textToLink.length

                    append(string)
                    addStyle(
                        SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        ),
                        start = linkStartIndex, end = linkEndIndex
                    )

                    addStringAnnotation(
                        "URL",
                        annotation = "https://sites.google.com/view/software-overflow/hangtight-privacy-policy",
                        start = linkStartIndex,
                        end = linkEndIndex
                    )
                }

                val uriHandler = LocalUriHandler.current

                ClickableText(text = annotatedLinkString, onClick = {
                    annotatedLinkString.getStringAnnotations("URL", it, it)
                        .firstOrNull()?.let{
                            annotation -> uriHandler.openUri(annotation.item)
                        }
                }, modifier = Modifier.align(CenterHorizontally), style = TextStyle.Default.copy(color = colors.onSurface))

                Spacer(Modifier.height(32.dp))

                Button({ onAccept() }) {
                    Text("I Accept")
                }
            }
        }

    }
}

@Preview
@Composable
private fun Preview_ConsentDialog() {
    AppTheme(darkTheme = false) {
        ConsentDialogContent {}
    }
}

@Preview
@Composable
private fun Preview_ConsentDialog_Dark() {
    AppTheme(darkTheme = true) {
        ConsentDialogContent {}
    }
}
