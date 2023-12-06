package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {

    private TaskDataSource dataSource;
    private ArrayAdapter<TaskModel> adapter;
    private static final int TASK_DETAIL_REQUEST_CODE = 1;
    private int currentIdCounter = 1; // Счетчик id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // Инициализация источника данных
        dataSource = new TaskDataSource(this);
        dataSource.open();

        // Добавление фрагмента SyncFragment в разметку
        SyncFragment syncFragment = new SyncFragment(dataSource);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, syncFragment);
        transaction.commit();

        // Получение списка задач из базы данных
        List<TaskModel> taskModels = dataSource.getAllTasks();

        // Инициализация адаптера для списка задач
        adapter = new ArrayAdapter<TaskModel>(this, R.layout.my_simple_list_item, taskModels) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.my_simple_list_item, parent, false);
                }

                TextView titleTextView = convertView.findViewById(R.id.titleTextView);
                TextView dateTextView = convertView.findViewById(R.id.dateTextView);

                TaskModel taskModel = getItem(position);

                if (titleTextView != null) {
                    if (taskModel != null && taskModel.getTitle() != null) {
                        titleTextView.setText(taskModel.getTitle());
                    } else {
                        titleTextView.setText("Нет данных");
                    }
                }
                Button deleteButton = convertView.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmationDialog(taskModel);
                    }
                });
                int startRedColor = Color.parseColor("#FF0000");
                int startGreenColor = Color.parseColor("#00FF00");
                int endColor = Color.parseColor("#d9d8e3");

                GradientDrawable redGradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{endColor, startRedColor, endColor});
                GradientDrawable greenGradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{endColor, startGreenColor, endColor});

                if (dateTextView != null) {
                    if (taskModel != null && taskModel.getDueDate() != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("   dd.MM.yyyy", Locale.getDefault());
                        dateTextView.setText(dateFormat.format(taskModel.getDueDate()));

                        long daysUntilDue = calculateDaysUntilDue(taskModel.getDueDate());

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

        // Обработка нажатия на элемент списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskModel selectedTaskModel = adapter.getItem(position);
                Intent intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
                intent.putExtra("taskId", selectedTaskModel.getId());
                startActivity(intent);
            }
        });

        final EditText titleEditText = findViewById(R.id.titleEditText);

        // Обработка нажатия на кнопку добавления задачи
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();

                if (title.trim().isEmpty()) {
                    Toast.makeText(TaskActivity.this, "Вы не ввели название", Toast.LENGTH_SHORT).show();
                } else {
                    long dateInMillis = System.currentTimeMillis();
                    TaskModel taskModel = new TaskModel(title, null, dateInMillis);
                    currentIdCounter++;

                    dataSource.addTask(taskModel);

                    adapter.add(taskModel);
                    adapter.notifyDataSetChanged();

                    titleEditText.setText("");
                }
            }
        });
    }

    // Метод для обновления пользовательского интерфейса с новой задачей
    public void updateUIWithNewTask(TaskModel newTask) {
        // Добавление новой задачи в список
        adapter.add(newTask);
        adapter.notifyDataSetChanged();
    }

    // Метод для создания элементов в приложении на основе данных с сервера
    private void createElementsFromServerData(List<TaskModel> serverTasks) {
        // Вывод в лог о начале создания элементов на основе данных с сервера
        Log.d("TaskActivity", "Начало создания элементов на основе данных с сервера");

        // Проход по списку задач с сервера и создание элементов в приложении
        for (TaskModel taskModel : serverTasks) {
            // Вывод в лог данных о задаче, которую вы создаете
            Log.d("TaskActivity", "Создание элемента на основе задачи с сервера: " + taskModel.getTitle());

            // Добавьте ваш код для создания элементов в приложении на основе данных с сервера
            // Например, добавьте их в вашу базу данных или отобразите на экране

            // Пример: добавление задачи в локальную базу данных
            dataSource.addTask(taskModel);

            // Пример: обновление пользовательского интерфейса для отображения новой задачи
            updateUIWithNewTask(taskModel);
        }

        // Вывод в лог об окончании создания элементов на основе данных с сервера
        Log.d("TaskActivity", "Завершение создания элементов на основе данных с сервера");
    }

    // Отображение диалога подтверждения удаления
    private void showDeleteConfirmationDialog(final TaskModel taskModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление")
                .setMessage("Вы точно хотите удалить \"" + taskModel.getTitle() + "\"?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dataSource.deleteTask(taskModel.getId());
                        adapter.remove(taskModel);
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
                TaskModel updatedTaskModel = dataSource.getTaskById(taskId);
                if (updatedTaskModel != null) {
                    int position = adapter.getPosition(updatedTaskModel);
                    if (position != -1) {
                        adapter.remove(updatedTaskModel);
                        adapter.insert(updatedTaskModel, position);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    // Рассчет количества дней до указанной даты
    private long calculateDaysUntilDue(Date dueDate) {
        long currentTimeMillis = System.currentTimeMillis();
        long dueTimeMillis = dueDate.getTime();
        return (dueTimeMillis - currentTimeMillis) / (24 * 60 * 60 * 1000);
    }

    // Обновление списка задач при возврате на экран
    @Override
    protected void onResume() {
        super.onResume();
        List<TaskModel> updatedTaskModels = dataSource.getAllTasks();
        adapter.clear();
        adapter.addAll(updatedTaskModels);
        adapter.notifyDataSetChanged();
    }

    // Закрытие источника данных при выходе из активности
    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }
}