package com.softwareoverflow.HangTight.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Locale;
import java.util.function.BiFunction;

/**
 * Creates an alert dialog which will show a confirmation message.
 * Negative button simply closes the dialog.
 * Positive button will call supplied lambda
 */
class AlertDialogConfirmation extends AlertDialog {

    AlertDialogConfirmation(Context context, String action, String detail, BiFunction<DialogInterface, Integer, Object> positiveCallback) {
        super(context);


        setTitle("Are you sure?");
        setMessage(String.format(Locale.getDefault(), "Are you sure you want to %s '%s'", action, detail));

        setButton(BUTTON_POSITIVE, "Yes - " + action, (positiveCallback::apply));

        setButton(BUTTON_NEGATIVE, "No", ((dialog, which) -> dialog.dismiss()));

        setCancelable(true);
    }


}
