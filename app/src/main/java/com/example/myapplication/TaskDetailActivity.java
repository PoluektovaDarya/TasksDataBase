package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {

    private TaskDataSource dataSource;
    private Task currentTask;
    private EditText editTextDescription;
    private TextView dateTextView;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        dataSource = new TaskDataSource(this);
        dataSource.open();

        initViews();
        initTaskData();
        setButtonClickListeners();

        editTextDescription = findViewById(R.id.editTextDescription);
        initTaskData();
        setButtonClickListeners();
    }

    private void initViews() {
        editTextDescription = findViewById(R.id.editTextDescription);
        dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        // Инициализируем выбранную дату
        selectedDate = Calendar.getInstance();
    }
    private void initTaskData() {
        Intent intent = getIntent();
        int taskId = intent.getIntExtra("taskId", -1);

        currentTask = dataSource.getTaskById(taskId);
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(currentTask.getTitle());

        // Отображаем текущую дату
        updateDateTextView();

        editTextDescription.setText(currentTask.getDescription());
    }

    private void setButtonClickListeners() {
        Button backButton = findViewById(R.id.backButton);
        Button saveButton = findViewById(R.id.saveButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDateOnFirstActivity();
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveDialog();
                updateDateOnFirstActivity();
            }
        });
    }
    private void showDatePickerDialog() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, day);

                        // Обновите поле с датой
                        updateDateTextView();
                    }
                },
                year, month, day);

        // Покажите диалог
        datePickerDialog.show();
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтверждение сохранения")
                .setMessage("Вы уверены, что хотите сохранить изменения?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveTaskDescription();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // ничего не делаем, оставляем окно открытым
                    }
                })
                .show();
    }
    private void updateDateTextView() {
        if (selectedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(" dd.MM.yyyy", Locale.getDefault());
            dateTextView.setText(dateFormat.format(selectedDate.getTime()));
        }
    }

    private void saveTaskDescription() {
        String description = editTextDescription.getText().toString();

        currentTask.setDescription(description);

        if (selectedDate != null) {
            currentTask.setDate(selectedDate.getTime());
        }

        dataSource.updateTask(currentTask);

        // Обновляем дату на первой активити
        updateDateOnFirstActivity();
    }
    private void updateDateOnFirstActivity() {
        if (selectedDate != null) {
            currentTask.setDate(selectedDate.getTime());
            dataSource.updateTask(currentTask);

            // Вместо использования setResult, передаем taskId напрямую
            Intent resultIntent = new Intent();
            resultIntent.putExtra("taskId", currentTask.getId());
            setResult(RESULT_OK, resultIntent);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }
}
