package com.softwareoverflow.HangTight.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.softwareoverflow.HangTight.R;
import com.softwareoverflow.HangTight.helper.SoftKeyboardHelper;
import com.softwareoverflow.HangTight.database.MyDBHandler;
import com.softwareoverflow.HangTight.workout.Workout;

/**
 * AlertDialog to show before saving a workout, prompting the user to enter a name and description for the workout.
 */
public class SaveWorkoutDialog extends AlertDialog
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Context context;
    private Workout workout;

    private ViewGroup rootView;

    private EditText etWorkoutName, etWorkoutDescription;
    private TextView saveFailedWarning, saveNew, overwriteExisting;
    private Switch saveTypeSwitch;

    public SaveWorkoutDialog(Context context, Workout workout) {
        super(context, R.style.CustomDialogTheme);

        this.context = context;
        this.workout = workout;

        setTitle("Save Workout");
        setIcon(android.R.drawable.ic_menu_save);
        setContentView(R.layout.dialog_save_workout);

        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_save_workout, rootView, false);

        ViewGroup saveTypeLayout = view.findViewById(R.id.save_workout_save_type_layout);
        if(workout.getId() == null) // The workout has not been saved before
            saveTypeLayout.setVisibility(View.GONE);

        // TODO - change @+id/ to @id/ where required (almost everywhere :/ )

        setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        // null listener - added onShow to allow validation without closing the dialog
        setButton(BUTTON_POSITIVE, "Save Workout", (OnClickListener) null);

        setView(view);

        setOnShowListener(dialog -> {
            etWorkoutName = ((AlertDialog) dialog).findViewById(R.id.editText_saveWorkout_workoutName);
            etWorkoutName.requestFocus();
            etWorkoutName.setText(workout.getWorkoutName());

            etWorkoutDescription = ((AlertDialog) dialog).findViewById(R.id.editText_saveWorkout_workoutDescription);
            etWorkoutDescription.setText(workout.getWorkoutDescription());

            saveFailedWarning = ((AlertDialog) dialog).findViewById(R.id.save_workout_failed_warning);

            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(SaveWorkoutDialog.this);

            overwriteExisting = ((AlertDialog) dialog).findViewById(R.id.save_workout_overwrite_existing);
            saveNew = ((AlertDialog) dialog).findViewById(R.id.save_workout_save_new);

            if(workout.getId() != null) { // The workout has been saved before
                saveTypeSwitch = ((AlertDialog) dialog).findViewById(R.id.save_workout_save_type_switch);
                saveTypeSwitch.setOnCheckedChangeListener(SaveWorkoutDialog.this);
                saveTypeSwitch.setEnabled(true);
            }

            Window window = getWindow();
            if(window != null)
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            overwriteExisting.setAlpha(1f);
            saveNew.setAlpha(0.3f);
        } else {
            overwriteExisting.setAlpha(0.3f);
            saveNew.setAlpha(1f);
        }
    }

    @Override
    public void onClick(View v) {
        String workoutName = etWorkoutName.getText().toString();

        if(workoutName.equals("")){
            saveFailedWarning.setVisibility(View.INVISIBLE);
            int color = v.getContext().getResources().getColor(R.color.Red, v.getContext().getTheme());
            setNameBackgroundColor(color);
            return;
        }

        String workoutDescription = etWorkoutDescription.getText().toString();

        workout.setWorkoutName(workoutName);
        workout.setWorkoutDescription(workoutDescription);

        MyDBHandler dbHandler = new MyDBHandler(context, null);
        boolean overwriteExisting = saveTypeSwitch != null && saveTypeSwitch.isChecked();
        dbHandler.addWorkout(workout, false, overwriteExisting, this::handleResult);
    }

    private void handleResult(boolean success){
        if(success){
            // Close dialog, hiding soft keyboard and showing a message
            dismiss();
            SoftKeyboardHelper.hide((Activity) context);

            Snackbar.make(rootView, "Workout saved!", Snackbar.LENGTH_SHORT).show();
        } else {
            int color = getContext().getResources().getColor(R.color.Blue, getContext().getTheme());
            setNameBackgroundColor(color);
            saveFailedWarning.setVisibility(View.VISIBLE);
        }
    }

    private void setNameBackgroundColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            etWorkoutName.getBackground().setColorFilter(new BlendModeColorFilter(R.color.Charcoal, BlendMode.SRC_ATOP));
        else
            etWorkoutName.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}
