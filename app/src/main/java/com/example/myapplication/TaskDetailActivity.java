package com.example.myapplication;

import android.annotation.SuppressLint;
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
    private TaskModel currentTaskModel;
    private EditText editTextDescription;
    private TextView dateTextView;
    private EditText titleEditText;
    private Calendar selectedDate;

    @SuppressLint("MissingInflatedId")
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
        titleEditText = findViewById(R.id.titleEditText);
        dateTextView = findViewById(R.id.dateTextView);

        updateDateTextView();
    }

    private void initViews() {
        editTextDescription = findViewById(R.id.editTextDescription);
        dateTextView = findViewById(R.id.dateTextView);
        titleEditText = findViewById(R.id.titleEditText);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        titleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleEditText.setFocusableInTouchMode(true);
                titleEditText.setFocusable(true);
            }
        });

        // Инициализируем выбранную дату
        selectedDate = Calendar.getInstance();
        updateDateTextView();
    }

    private void initTaskData() {
        Intent intent = getIntent();
        int taskId = intent.getIntExtra("taskId", -1);

        currentTaskModel = dataSource.getTaskById(taskId);
        titleEditText.setText(currentTaskModel.getTitle());
        editTextDescription.setText(currentTaskModel.getDescription());
    }

    private void setButtonClickListeners() {
        Button backButton = findViewById(R.id.backButton);
        Button saveButton = findViewById(R.id.saveButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveDialog();
                saveTaskDescription();
                updateDateOnFirstActivity();
                updateDateTextView();
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
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтверждение сохранения")
                .setMessage("Вы уверены, что хотите сохранить изменения?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Скрываем клавиатуру при сохранении
                        titleEditText.setFocusable(false);
                        titleEditText.setFocusableInTouchMode(false);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Ничего не делаем, оставляем окно открытым
                    }
                })
                .show();
    }

    private void updateDateTextView() {
        if (currentTaskModel != null && currentTaskModel.getDueDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(" dd.MM.yyyy", Locale.getDefault());
            dateTextView.setText(dateFormat.format(currentTaskModel.getDueDate()));
        }
    }

    private void saveTaskDescription() {
        String description = editTextDescription.getText().toString();
        String title = titleEditText.getText().toString();

        currentTaskModel.setDescription(description);
        currentTaskModel.setTitle(title);

        if (selectedDate != null) {
            currentTaskModel.setDate(selectedDate.getTime());
        }

        dataSource.updateTask(currentTaskModel);
        updateDateOnFirstActivity();
    }

    private void updateDateOnFirstActivity() {
        if (selectedDate != null) {
            currentTaskModel.setDate(selectedDate.getTime());
            dataSource.updateTask(currentTaskModel);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("taskId", currentTaskModel.getId());
            setResult(RESULT_OK, resultIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }
}