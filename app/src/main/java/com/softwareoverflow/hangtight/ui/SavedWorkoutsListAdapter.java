package com.softwareoverflow.hangtight.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.google.android.material.snackbar.Snackbar;
import com.softwareoverflow.hangtight.ActivityWorkout;
import com.softwareoverflow.hangtight.ActivityWorkoutCreator;
import com.softwareoverflow.hangtight.R;
import com.softwareoverflow.hangtight.helper.StringHelper;
import com.softwareoverflow.hangtight.ui.dialog.ConfirmationDialog;
import com.softwareoverflow.hangtight.ui.dialog.WarmUpWarningDialog;
import com.softwareoverflow.hangtight.database.MyDBHandler;
import com.softwareoverflow.hangtight.workout.Workout;

import java.util.List;

public class SavedWorkoutsListAdapter extends RecyclerView.Adapter<SavedWorkoutsListAdapter.ViewHolder> {

    private MyDBHandler dbHandler;
    private RecyclerView recyclerView;
    private List<Workout> workouts;
    private int expandedPosition = -1;

    private TransitionSet transitions;

    public SavedWorkoutsListAdapter(MyDBHandler dbHandler) {
        super();

        this.workouts = dbHandler.loadAllWorkouts();
        this.dbHandler = dbHandler;

        transitions = new TransitionSet();
        transitions.addTransition(new ChangeTransform()); // Button rotation
        transitions.addTransition(new AutoTransition()); // Expanded view visibility
    }

    public void updateDataSet(){
        this.workouts = dbHandler.loadAllWorkouts();
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View row = inflater.inflate(R.layout.row_saved_workouts, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Workout workout = workouts.get(position);

        holder.workoutNameTV.setText(workout.getWorkoutName());

        holder.workoutDurationTV.setText(StringHelper.minuteSecondTimeFormat(workout.getDuration()));
        holder.workoutDescriptionTV.setText(workout.getWorkoutDescription());

        final boolean isExpanded = expandedPosition == position;
        holder.expandedView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.extendViewIcon.setRotation(isExpanded ? 180 : 0);
        holder.itemView.setElevation( isExpanded ? 25 : 10);

        holder.itemView.setOnClickListener((v) -> {
            int oldExpandedPosition = expandedPosition;
            expandedPosition = isExpanded ? -1 : position;
            TransitionManager.beginDelayedTransition(recyclerView, transitions);

            // IF no item expanded, oldExpandedPosition = -1 and NewExpandedPos -> end needs updating
            // IF item expanded, either newly expanded or currently expanded -> end needs updating

            int startItem = oldExpandedPosition == -1 ? expandedPosition : Math.min(oldExpandedPosition, expandedPosition);
            notifyItemRangeChanged(startItem, getItemCount() - startItem);
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View itemView;
        TextView workoutNameTV, workoutDurationTV, workoutDescriptionTV;
        ConstraintLayout deleteWorkoutButton, editWorkoutButton, startWorkoutButton, expandedView;
        ImageView extendViewIcon;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;

            workoutNameTV = itemView.findViewById(R.id.row_savedWorkout_workoutName);
            workoutDurationTV = itemView.findViewById(R.id.row_savedWorkout_duration);
            workoutDescriptionTV = itemView.findViewById(R.id.row_savedWorkout_description);

            deleteWorkoutButton = itemView.findViewById(R.id.row_savedWorkout_delete);
            editWorkoutButton = itemView.findViewById(R.id.row_savedWorkout_edit);
            startWorkoutButton = itemView.findViewById(R.id.row_savedWorkout_start);

            deleteWorkoutButton.setOnClickListener(this);
            editWorkoutButton.setOnClickListener(this);
            startWorkoutButton.setOnClickListener(this);

            expandedView = itemView.findViewById(R.id.row_savedWorkout_expandedView);

            extendViewIcon = itemView.findViewById(R.id.row_savedWorkout_expandIcon);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.row_savedWorkout_delete:
                    new ConfirmationDialog(v.getContext(), "delete", workouts.get(getAdapterPosition()).getWorkoutName(), (DialogInterface dialog, Integer which) -> {
                        if (dbHandler.deleteWorkout(getAdapterPosition())) Snackbar.make(recyclerView, "Workout Deleted", Snackbar.LENGTH_SHORT).show();
                        else Snackbar.make(recyclerView, "Problem Deleting ActivityWorkout.\nPlease try again later", Snackbar.LENGTH_SHORT).show();

                        workouts.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    }).show();
                    break;

                case R.id.row_savedWorkout_edit:
                     Intent editWorkoutIntent = new Intent(v.getContext(), ActivityWorkoutCreator.class);
                     editWorkoutIntent.putExtra("workout", workouts.get(getAdapterPosition()));
                     v.getContext().startActivity(editWorkoutIntent);
                    break;

                case R.id.row_savedWorkout_start:
                    Intent i = new Intent(v.getContext(), ActivityWorkout.class);
                    i.putExtra("workout", workouts.get(getAdapterPosition()));
                    new WarmUpWarningDialog(v.getContext(), i).show(); // Creates and shows the warm up warning (if required)
                    break;
            }
        }
    }
}
