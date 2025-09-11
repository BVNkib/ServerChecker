package ru.kb.lt.serverchecker.backgrounds;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ru.kb.lt.serverchecker.model.Server;
import ru.kb.lt.serverchecker.repository.ServerChecker;
import ru.kb.lt.serverchecker.repository.ServersRepository;

public class ServerMonitorWorker extends Worker {
    private static final String TAG = "ServerMonitorWorker";
    private static final String WORK_NAME = "ServerMonitorWork";

    private final ServersRepository serversRepository;

    public ServerMonitorWorker(
            @NonNull Application application,
            @NonNull WorkerParameters workerParams) {
        super(application.getApplicationContext(), workerParams);
        Log.d(TAG, "Create worker!");
        serversRepository = new ServersRepository(application);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Start worker");
        try {
            List<Server> servers = serversRepository.getAllServersSync();

            if (servers.isEmpty()) {
                Log.d(TAG, "No servers in worker");
                return Result.success();
            }

            for (Server server : servers) {
                if (!ServerChecker.checkServerAvailability(server)) {
                    NotificationUtils
                            .showServerDownNotification(
                                    getApplicationContext(),
                                    server.getName(),
                                    server.getId());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            return Result.retry();
        }

        return Result.success();
    }

    public static void scheduleWork(Context context) {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                ServerMonitorWorker.class,
                15,
                TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
        );
    }
}