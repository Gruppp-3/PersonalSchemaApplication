package com.example.personalschemaapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalschemaapplication.R;
import com.example.personalschemaapplication.WorkShift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkShiftAdapter extends RecyclerView.Adapter<WorkShiftAdapter.WorkShiftViewHolder> {

    private static final String TAG = "WorkShiftAdapter";
    private List<WorkShift> workShifts;
    private Context context;
    private OnShiftActionListener listener;
    private boolean isEmployeeView = true; // Set to true for the employee app

    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat displayDayFormat = new SimpleDateFormat("EEE d MMM", Locale.getDefault());

    public interface OnShiftActionListener {
        void onDeleteShift(WorkShift workShift);
        void onAssignEmployee(WorkShift workShift);
    }

    public WorkShiftAdapter(List<WorkShift> workShifts, Context context, OnShiftActionListener listener) {
        this.workShifts = workShifts;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work_shift, parent, false);
        return new WorkShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkShiftViewHolder holder, int position) {
        WorkShift workShift = workShifts.get(position);

        try {
            // Parse dates for display
            Date startDate = apiDateFormat.parse(workShift.getStartTime());
            Date endDate = apiDateFormat.parse(workShift.getEndTime());

            if (startDate != null && endDate != null) {
                // Set the date (e.g., "Mon 15 Apr")
                holder.tvShiftDate.setText(displayDayFormat.format(startDate));

                // Set the time (e.g., "08:00 - 16:00")
                String timeRange = displayTimeFormat.format(startDate) +
                        " - " +
                        displayTimeFormat.format(endDate);
                holder.tvShiftTime.setText(timeRange);
            } else {
                // Fallback if parsing fails
                holder.tvShiftDate.setText(workShift.getStartTime().substring(0, 10));
                holder.tvShiftTime.setText("Time not available");
            }
        } catch (ParseException e) {
            // Fallback if parsing fails
            holder.tvShiftDate.setText(workShift.getStartTime().substring(0, 10));
            holder.tvShiftTime.setText("Time not available");
        }

        // Set employee name
        if (workShift.getEmployee() != null &&
                workShift.getEmployee().getFirstName() != null &&
                workShift.getEmployee().getLastName() != null) {

            String employeeName = workShift.getEmployee().getFirstName() + " " +
                    workShift.getEmployee().getLastName();
            holder.tvEmployeeName.setText(employeeName);

            if (isEmployeeView) {
                holder.btnAssignEmployee.setText("Byt pass"); // "Swap shift"
                holder.btnDeleteShift.setVisibility(View.GONE); // Hide delete button in employee view
            } else {
                holder.btnAssignEmployee.setText("Ändra"); // "Edit"
                holder.btnDeleteShift.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "Shift ID: " + workShift.getId() +
                    " assigned to employee: " + employeeName +
                    " (ID: " + workShift.getEmployee().getId() + ")");
        } else {
            holder.tvEmployeeName.setText("Ej tilldelad");

            if (isEmployeeView) {
                holder.btnAssignEmployee.setText("Ta pass"); // "Take shift"
                holder.btnDeleteShift.setVisibility(View.GONE); // Hide delete button in employee view
            } else {
                holder.btnAssignEmployee.setText("Tilldela"); // "Assign"
                holder.btnDeleteShift.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "Shift ID: " + workShift.getId() + " is unassigned");
        }

        // Set button listeners with logging for debugging
        holder.btnDeleteShift.setOnClickListener(v -> {
            if (listener != null) {
                Log.d(TAG, "Delete button clicked for shift ID: " + workShift.getId());
                listener.onDeleteShift(workShift);
            }
        });

        holder.btnAssignEmployee.setOnClickListener(v -> {
            if (listener != null) {
                Log.d(TAG, "Assign/Change button clicked for shift ID: " + workShift.getId());
                listener.onAssignEmployee(workShift);
            }
        });

        // Add click listener for the entire item view
        holder.itemView.setOnClickListener(v -> {
            // Show action dialog when item is clicked
            showWorkshiftActionDialog(workShift);
        });
    }

    private void showWorkshiftActionDialog(WorkShift workShift) {
        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Hantera arbetspass");

        // Format shift info for dialog
        try {
            Date startDate = apiDateFormat.parse(workShift.getStartTime());
            Date endDate = apiDateFormat.parse(workShift.getEndTime());

            if (startDate != null && endDate != null) {
                String dateStr = displayDayFormat.format(startDate);
                String timeStr = displayTimeFormat.format(startDate) +
                        " - " +
                        displayTimeFormat.format(endDate);

                String employeeStr;
                if (workShift.getEmployee() != null) {
                    employeeStr = workShift.getEmployee().getFirstName() +
                            " " +
                            workShift.getEmployee().getLastName();
                } else {
                    employeeStr = "Ej tilldelad";
                }

                builder.setMessage(dateStr + "\n" + timeStr + "\n" + employeeStr);
            }
        } catch (ParseException e) {
            // Fallback message if parsing fails
            builder.setMessage("ID: " + workShift.getId());
        }

        // Add action buttons with specific text for employee view
        if (isEmployeeView) {
            if (workShift.getEmployee() == null) {
                // Unassigned shift
                builder.setPositiveButton("Ta pass", (dialog, which) -> {
                    if (listener != null) {
                        listener.onAssignEmployee(workShift);
                    }
                });
            } else {
                // Assigned shift (either to this employee or another)
                builder.setPositiveButton("Begär byte", (dialog, which) -> {
                    if (listener != null) {
                        listener.onAssignEmployee(workShift);
                    }
                });
            }
            // Don't add delete button for employee view
        } else {
            // Admin view - use original behavior
            builder.setPositiveButton(workShift.getEmployee() != null ? "Ändra tilldelning" : "Tilldela till anställd",
                    (dialog, which) -> {
                        if (listener != null) {
                            listener.onAssignEmployee(workShift);
                        }
                    });

            builder.setNeutralButton("Ta bort arbetspass", (dialog, which) -> {
                if (listener != null) {
                    listener.onDeleteShift(workShift);
                }
            });
        }

        builder.setNegativeButton("Avbryt", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return workShifts.size();
    }

    // Set whether this is being used in the employee view or admin view
    public void setEmployeeView(boolean isEmployeeView) {
        this.isEmployeeView = isEmployeeView;
    }

    static class WorkShiftViewHolder extends RecyclerView.ViewHolder {
        TextView tvShiftDate;
        TextView tvShiftTime;
        TextView tvEmployeeName;
        Button btnAssignEmployee;
        Button btnDeleteShift;

        WorkShiftViewHolder(View itemView) {
            super(itemView);
            tvShiftDate = itemView.findViewById(R.id.tvShiftDate);
            tvShiftTime = itemView.findViewById(R.id.tvShiftTime);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            btnAssignEmployee = itemView.findViewById(R.id.btnAssignEmployee);
            btnDeleteShift = itemView.findViewById(R.id.btnDeleteShift);
        }
    }
}
