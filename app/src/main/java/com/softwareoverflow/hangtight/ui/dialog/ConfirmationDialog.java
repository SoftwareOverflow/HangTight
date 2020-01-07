package com.softwareoverflow.hangtight.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * Creates an alert dialog which will show a confirmation message.
 * Negative button simply closes the dialog.
 * Positive button will call supplied lambda
 */
public class ConfirmationDialog extends AlertDialog {

    public ConfirmationDialog(Context context, String action, String detail, BiConsumer<DialogInterface, Integer> positiveCallback) {
        super(context);

        setTitle("Are you sure?");
        setIcon(android.R.drawable.ic_dialog_alert);
        setMessage(String.format(Locale.getDefault(), "Are you sure you want to %s '%s'", action, detail));

        setButton(BUTTON_POSITIVE, "Yes - " + action, (positiveCallback::accept));
        setButton(BUTTON_NEGATIVE, "No", ((dialog, which) -> dialog.dismiss()));

        setCancelable(true);
    }


}
