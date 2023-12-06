package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncFragment extends Fragment {

    // Источник данных для локальных задач
    private TaskDataSource dataSource;

    // Конструктор для инициализации фрагмента с TaskDataSource
    public SyncFragment(TaskDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Переопределение метода onCreateView для инфляции макета фрагмента
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Инфлирование макета для данного фрагмента
        View view = inflater.inflate(R.layout.fragment_sync, container, false);

        // Установка слушателя OnClickListener для кнопки syncButton в макете
        view.findViewById(R.id.syncButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Вызов метода syncWithServer при нажатии на кнопку
                syncWithServer();
            }
        });

        // Возврат инфлированного представления
        return view;
    }

    // Метод для синхронизации данных с сервера
    private void syncWithServer() {
        // Создание экземпляра ServerApiManager
        ServerApiManager apiManager = new ServerApiManager();

        // Вывод в лог о начале синхронизации
        Log.d("SyncFragment", "Начало синхронизации с сервером");

        // Получение данных с сервера
        apiManager.getTasks(new Callback<List<TaskModel>>() {
            @Override
            public void onResponse(Call<List<TaskModel>> call, Response<List<TaskModel>> response) {
                if (response.isSuccessful()) {
                    // Обработка успешного получения данных с сервера
                    List<TaskModel> serverTasks = response.body();

                    // Создание элементов в приложении на основе данных с сервера
                    createElementsFromServerData(serverTasks);

                    // Отправка отсутствующих задач на сервер
                    sendMissingTasksToServer(apiManager, getAllTasksFromDB(), serverTasks);

                    // Вывод в лог об успешной синхронизации
                    Log.d("SyncFragment", "Успешная синхронизация с сервером");
                } else {
                    // Логирование ошибки при получении данных с сервера
                    Log.e("SyncFragment", "Ошибка при получении данных с сервера: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<TaskModel>> call, Throwable t) {
                // Логирование неудачи при сетевом запросе для получения задач
                Log.e("SyncFragment", "Ошибка при получении данных с сервера: " + t.getMessage());
            }
        });
    }

    // Метод для создания элементов в приложении на основе данных с сервера
    private void createElementsFromServerData(List<TaskModel> serverTasks) {
        // Вывод в лог о начале создания элементов на основе данных с сервера
        Log.d("SyncFragment", "Начало создания элементов на основе данных с сервера");

        // Проход по списку задач с сервера и создание элементов в приложении
        for (TaskModel taskModel : serverTasks) {
            // Вывод в лог данных о задаче, которую вы создаете
            Log.d("SyncFragment", "Создание элемента на основе задачи с сервера: " + taskModel.getTitle());

            // Добавление задачи в локальную базу данных
            dataSource.addTask(taskModel);

        }

        // Вывод в лог об окончании создания элементов на основе данных с сервера
        Log.d("SyncFragment", "Завершение создания элементов на основе данных с сервера");
    }

    // Метод для отправки отсутствующих задач на сервер
    private void sendMissingTasksToServer(ServerApiManager apiManager, List<TaskModel> localTasks, List<TaskModel> serverTasks) {
        // Поиск отсутствующих задач
        List<TaskModel> missingTasks = findMissingTasks(localTasks, serverTasks);

        // Отправка отсутствующих задач на сервер
        for (TaskModel taskModel : missingTasks) {
            apiManager.createTask(taskModel.getTitle(), taskModel.formatDateTimeToString(),
                    new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            // Логирование успешной или неудачной отправки данных на сервер
                            if (response.isSuccessful()) {
                                Log.d("SyncFragment", "Данные успешно отправлены на сервер");
                            } else {
                                Log.e("SyncFragment", "Ошибка при отправке данных на сервер: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // Логирование неудачи при отправке данных на сервер
                            Log.e("SyncFragment", "Ошибка при отправке данных на сервер: " + t.getMessage());
                        }
                    });
        }
    }

    // Метод для поиска отсутствующих задач
    private List<TaskModel> findMissingTasks(List<TaskModel> localTasks, List<TaskModel> serverTasks) {
        List<TaskModel> missingTasks = new ArrayList<>();

        for (TaskModel localTask : localTasks) {
            if (!containsTask(serverTasks, localTask)) {
                missingTasks.add(localTask);
            }
        }

        return missingTasks;
    }

    // Метод для проверки наличия задачи в списке задач
    private boolean containsTask(List<TaskModel> taskList, TaskModel task) {
        for (TaskModel t : taskList) {
            if (t.getTitle().equals(task.getTitle())) {
                return true;
            }
        }
        return false;
    }

    // Метод для получения всех задач из локальной базы данных
    private List<TaskModel> getAllTasksFromDB() {
        return dataSource.getAllTasks();
    }
}
