package com.softwareoverflow.HangTight.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;

import com.softwareoverflow.HangTight.R;

import static android.content.Context.MODE_PRIVATE;


// TODO - change all string to resources

/**
 * Creates and shows the warm up warning dialog to the user, if they haven't previously turned it off
 */
public class WarmUpWarningDialog extends AlertDialog {

    private boolean showWarning;

    public WarmUpWarningDialog(Context context, Intent intent) {
        super(context, R.style.CustomDialogTheme);

        final SharedPreferences settings = context.getSharedPreferences("settings", MODE_PRIVATE);
        showWarning = settings.getBoolean("showWarmUp", true);

        if (!showWarning) {
            context.startActivity(intent);
        }

        ViewGroup rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_warm_up_warning_checkbox, rootView, false);

        CheckBox dialog_warm_up_warning_checkbox = view.findViewById(R.id.checkbox);
        dialog_warm_up_warning_checkbox.setSelected(!showWarning);
        dialog_warm_up_warning_checkbox.setOnClickListener(v -> showWarning = v.isSelected());

        setTitle(getContext().getString(R.string.dialog_warm_up_title));
        setIcon(android.R.drawable.ic_dialog_alert);
        setMessage(getContext().getString(R.string.dialog_warm_up_message));
        setView(view);

        setButton(BUTTON_POSITIVE, getContext().getString(R.string.dialog_warm_up_start_workout), (DialogInterface dialog, int which) -> {
                    settings.edit().putBoolean("showWarmUp", showWarning).apply();
                    context.startActivity(intent);
                }
        );
        setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel), (dialog, which) -> dialog.cancel());
    }
}
