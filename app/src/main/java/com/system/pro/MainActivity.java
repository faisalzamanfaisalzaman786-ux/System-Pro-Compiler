package com.system.pro;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SystemProPrefs";
    private static final String TASKS_KEY = "tasks";

    private List<Task> taskList;
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerViewTasks;
    private EditText editTextNewTask;
    private FloatingActionButton fabAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("System Pro - ToDo");
        }

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        editTextNewTask = findViewById(R.id.editTextNewTask);
        fabAddTask = findViewById(R.id.fabAddTask);

        taskList = loadTasks();
        taskAdapter = new TaskAdapter(this, taskList);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        fabAddTask.setOnClickListener(v -> addTask());
    }

    private void addTask() {
        String taskDescription = editTextNewTask.getText().toString().trim();
        if (!taskDescription.isEmpty()) {
            Task newTask = new Task(taskDescription, false);
            taskList.add(newTask);
            taskAdapter.notifyItemInserted(taskList.size() - 1);
            editTextNewTask.setText("");
            saveTasks();
            Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleTaskCompletion(int position) {
        Task task = taskList.get(position);
        task.setCompleted(!task.isCompleted());
        taskAdapter.notifyItemChanged(position);
        saveTasks();
    }

    private void deleteTask(int position) {
        taskList.remove(position);
        taskAdapter.notifyItemRemoved(position);
        saveTasks();
        Toast.makeText(this, "Task deleted!", Toast.LENGTH_SHORT).show();
    }

    private void saveTasks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(taskList);
        editor.putString(TASKS_KEY, json);
        editor.apply();
    }

    private List<Task> loadTasks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(TASKS_KEY, null);
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> loadedList = gson.fromJson(json, type);
        return loadedList == null ? new ArrayList<>() : loadedList;
    }

    // Task Model Class
    private static class Task {
        private String description;
        private boolean completed;

        public Task(String description, boolean completed) {
            this.description = description;
            this.completed = completed;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }

    // RecyclerView Adapter
    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

        private Context context;
        private List<Task> tasks;

        public TaskAdapter(Context context, List<Task> tasks) {
            this.context = context;
            this.tasks = tasks;
        }

        @Override
        public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.checkBoxCompleted.setChecked(task.isCompleted());
            holder.textViewDescription.setText(task.getDescription());

            // Apply strike-through if completed
            if (task.isCompleted()) {
                holder.textViewDescription.setPaintFlags(holder.textViewDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.textViewDescription.setPaintFlags(holder.textViewDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                toggleTaskCompletion(holder.getAdapterPosition());
            });

            holder.imageButtonDelete.setOnClickListener(v -> {
                deleteTask(holder.getAdapterPosition());
            });
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        public class TaskViewHolder extends RecyclerView.ViewHolder {
            CheckBox checkBoxCompleted;
            TextView textViewDescription;
            ImageButton imageButtonDelete;

            public TaskViewHolder(View itemView) {
                super(itemView);
                checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
                textViewDescription = itemView.findViewById(R.id.textViewTaskDescription);
                imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);
            }
        }
    }
}