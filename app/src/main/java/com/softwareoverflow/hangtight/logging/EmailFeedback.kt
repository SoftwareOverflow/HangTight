package com.softwareoverflow.hangtight.logging

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.softwareoverflow.hangtight.ui.util.SnackbarManager

object EmailFeedback {

    private const val email = "SoftwareOverflow@gmail.com"
    private const val subject = "HangTight Hangboard Trainer Feedback"

    fun launch(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$email") // only email apps should handle this
        /*intent.putExtra(Intent.EXTRA_EMAIL, email)*/
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)

        try {
            startActivity(context, intent, null)
        } catch (ex: ActivityNotFoundException) {
            SnackbarManager.showMessage("No email clients found.")
        }
    }
}