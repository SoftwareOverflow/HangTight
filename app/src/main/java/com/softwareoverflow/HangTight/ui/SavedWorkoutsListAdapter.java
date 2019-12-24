package com.softwareoverflow.HangTight.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.softwareoverflow.HangTight.ActivityWorkout;
import com.softwareoverflow.HangTight.ActivityWorkoutCreator;
import com.softwareoverflow.HangTight.R;
import com.softwareoverflow.HangTight.helper.StringHelper;
import com.softwareoverflow.HangTight.workout.MyDBHandler;
import com.softwareoverflow.HangTight.workout.Workout;

import java.util.List;

public class SavedWorkoutsListAdapter extends RecyclerView.Adapter<SavedWorkoutsListAdapter.ViewHolder> {

    private MyDBHandler dbHandler;
    private RecyclerView recyclerView;
    private List<Workout> workouts;
    private int expandedPosition = -1;

    private TransitionSet transitions;

    public SavedWorkoutsListAdapter(List<Workout> workouts, MyDBHandler dbHandler) {
        super();

        this.workouts = workouts;
        this.dbHandler = dbHandler;

        transitions = new TransitionSet();
        transitions.addTransition(new ChangeTransform()); // Button rotation
        transitions.addTransition(new AutoTransition()); // Expanded view visibility
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
            expandedPosition = isExpanded ? -1 : position;
            TransitionManager.beginDelayedTransition(recyclerView, transitions);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View itemView;
        TextView workoutNameTV, workoutDurationTV, workoutDescriptionTV;
        ImageButton deleteWorkoutButton, editWorkoutButton, startWorkoutButton;
        ImageView extendViewIcon;

        ConstraintLayout expandedView;


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
                        // TODO - show 'Are you sure?' message
                    new AlertDialogConfirmation(v.getContext(), "delete", workouts.get(getAdapterPosition()).getWorkoutName(), (DialogInterface dialog, Integer which) -> {
                        if (dbHandler.deleteWorkout(getAdapterPosition())) Snackbar.make(recyclerView, "Workout Deleted", Snackbar.LENGTH_SHORT).show();
                        else Snackbar.make(recyclerView, "Problem Deleting ActivityWorkout.\nPlease try again later", Snackbar.LENGTH_SHORT).show();

                        workouts.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        // TODO -- is there a better option than BiFunction as we aren't using a return value?
                        return null;
                    }).show();
                    break;
                case R.id.row_savedWorkout_edit:
                     // TODO - Send user back to ActivityWorkoutCreator with extra flag on intent? Or add flag to Workout class for if it is saved or not?

                     Intent editWorkoutIntent = new Intent(v.getContext(), ActivityWorkoutCreator.class);
                     editWorkoutIntent.putExtra("workout", workouts.get(getAdapterPosition()));
                     v.getContext().startActivity(editWorkoutIntent);
                    break;
                case R.id.row_savedWorkout_start:
                    Intent i = new Intent(v.getContext(), ActivityWorkout.class);
                    i.putExtra("workout", workouts.get(getAdapterPosition()));
                    v.getContext().startActivity(i);
                    break;

            }
        }
    }
}
