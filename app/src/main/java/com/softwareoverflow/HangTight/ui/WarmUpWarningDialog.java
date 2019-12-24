package com.softwareoverflow.HangTight.ui;

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
public class WarmUpWarningDialog extends AlertDialog.Builder {

    private boolean showWarning;
    private AlertDialog dialog;

    public WarmUpWarningDialog(Context context, Intent intent) {
        super(context, R.style.CustomDialogTheme);

        final SharedPreferences settings = context.getSharedPreferences("settings", MODE_PRIVATE);
        showWarning = settings.getBoolean("showWarmUp", true);

        if (!showWarning) {
            context.startActivity(intent);
        }

        ViewGroup rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        View view = LayoutInflater.from(context).inflate(R.layout.checkbox, rootView, false);

        CheckBox checkbox = view.findViewById(R.id.checkbox);
        checkbox.setSelected(!showWarning);
        checkbox.setOnClickListener(v -> showWarning = v.isSelected());

        setTitle("Have You Warmed Up?");
        setMessage("Ensure you are thoroughly warmed up before beginning any" +
                " workout. Failure to do so could result in injury.\n\nIf you feel" +
                " any pain during the workout, discontinue immediately.");
        setView(view);

        setPositiveButton("Start Workout", (DialogInterface dialog, int which) -> {
                    settings.edit().putBoolean("showWarmUp", showWarning).apply();
                    context.startActivity(intent);
                }
        );
        setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
    }
}
