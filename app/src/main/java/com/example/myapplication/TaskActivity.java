package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {

    private TaskDataSource dataSource;
    private ArrayAdapter<Task> adapter;
    private static final int TASK_DETAIL_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        dataSource = new TaskDataSource(this);
        dataSource.open();

        List<Task> tasks = dataSource.getAllTasks();

        adapter = new ArrayAdapter<Task>(this, R.layout.my_simple_list_item, tasks) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.my_simple_list_item, parent, false);
                }

                TextView titleTextView = convertView.findViewById(R.id.titleTextView);
                TextView dateTextView = convertView.findViewById(R.id.dateTextView);

                Task task = getItem(position);

                if (titleTextView != null) {
                    if (task != null && task.getTitle() != null) {
                        titleTextView.setText(task.getTitle());
                    } else {
                        titleTextView.setText("Нет данных");
                    }
                }
                Button deleteButton = convertView.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmationDialog(task);
                    }
                });
                int startRedColor = Color.parseColor("#FF0000");
                int startGreenColor = Color.parseColor("#00FF00");
                int endColor = Color.parseColor("#d9d8e3");

                GradientDrawable redGradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{endColor, startRedColor, endColor});
                GradientDrawable greenGradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{endColor, startGreenColor, endColor});

                if (dateTextView != null) {
                    if (task != null && task.getDueDate() != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("   dd.MM.yyyy", Locale.getDefault());
                        dateTextView.setText(dateFormat.format(task.getDueDate()));

                        long daysUntilDue = calculateDaysUntilDue(task.getDueDate());

                        if (daysUntilDue <= 10) {
                            dateTextView.setBackground(redGradient);
                        } else {
                            dateTextView.setBackground(greenGradient);
                        }
                    } else {
                        dateTextView.setText("Нет данных");
                    }
                }

                return convertView;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };


        final ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task selectedTask = adapter.getItem(position);
                // Запускаем вторую активити с использованием startActivityForResult
                Intent intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
                intent.putExtra("taskId", selectedTask.getId());
                startActivity(intent);
            }
        });

        final EditText titleEditText = findViewById(R.id.titleEditText);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();

                if (title.trim().isEmpty()) {
                    Toast.makeText(TaskActivity.this, "Вы не ввели название", Toast.LENGTH_SHORT).show();
                } else {
                    // Создаем задачу и добавляем её только если введено хотя бы одно символ
                    long dateInMillis = System.currentTimeMillis();
                    Task task = new Task(title, null, null, dateInMillis);

                    dataSource.addTask(task);

                    adapter.add(task);
                    adapter.notifyDataSetChanged();

                    titleEditText.setText("");
                }
            }
        });

    }

    private void showDeleteConfirmationDialog(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление")
                .setMessage("Вы точно хотите удалить \"" + task.getTitle() + "\"?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dataSource.deleteTask(task.getId());
                        adapter.remove(task);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TASK_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            int taskId = data.getIntExtra("taskId", -1);
            if (taskId != -1) {
                Task updatedTask = dataSource.getTaskById(taskId);
                if (updatedTask != null) {
                    // Обновляем задачу в списке
                    int position = adapter.getPosition(updatedTask);
                    if (position != -1) {
                        adapter.remove(updatedTask);
                        adapter.insert(updatedTask, position);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
    private Task getTaskWithUpdatedDate(Date updatedDate) {
        for (int i = 0; i < adapter.getCount(); i++) {
            Task task = adapter.getItem(i);
            if (task != null && task.getDueDate() != null && task.getDueDate().equals(updatedDate)) {
                task.setDate(updatedDate);

                // Обновляем задачу в адаптере
                adapter.notifyDataSetChanged();

                return task;
            }
        }
        return null;
    }
    private long calculateDaysUntilDue(Date dueDate) {
        long currentTimeMillis = System.currentTimeMillis();
        long dueTimeMillis = dueDate.getTime();
        return (dueTimeMillis - currentTimeMillis) / (24 * 60 * 60 * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Task> updatedTasks = dataSource.getAllTasks();
        adapter.clear();
        adapter.addAll(updatedTasks);
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }
}
