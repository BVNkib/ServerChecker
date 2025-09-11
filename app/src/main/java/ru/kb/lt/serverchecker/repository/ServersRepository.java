package ru.kb.lt.serverchecker.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ru.kb.lt.serverchecker.model.Server;
import ru.kb.lt.serverchecker.repository.database.AppDatabase;
import ru.kb.lt.serverchecker.repository.database.ServerDao;

public class ServersRepository {
    private static final String TAG = "ServersRepository" ;

    private final ServerDao serverDao;
    private final LiveData<List<Server>> allServers;
    private final ExecutorService executorService;

    public ServersRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        serverDao = database.serverDao();
        allServers = serverDao.getAllServers();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Server>> getAllServers() {
        return allServers;
    }

    public List<Server> getAllServersSync() {
        Callable<List<Server>> callable = serverDao::getAllServersSync;
        Future<List<Server>> future = executorService.submit(callable);

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Some exceptions in getAllServersSync " + e.getMessage());
            return null;
        }
    }

    public void insert(Server server) {
        executorService.execute(() -> serverDao.insert(server));
    }

    public void update(Server server) {
        executorService.execute(() -> serverDao.update(server));
    }

    public void delete(Server server) {
        executorService.execute(() -> serverDao.delete(server));
    }
}
