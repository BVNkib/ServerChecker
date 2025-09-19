package ru.kb.lt.serverchecker.backgrounds;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
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
    private static final String WORK_NAME = "WORK_NAME_LOL";

    private final ServersRepository serversRepository;

    public ServerMonitorWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d(TAG, "Create worker!");
        Application application = (Application) context.getApplicationContext();
        serversRepository = new ServersRepository(application);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Start worker");
        NotificationUtils.createNotificationChannel(getApplicationContext());
        try {
            List<Server> servers = serversRepository.getAllServersSync();
            if (servers.isEmpty()) {
                Log.d(TAG, "No servers in worker");
                return Result.failure();
            }

            for (Server server : servers) {
                if (!ServerChecker.checkServerAvailability(server)) {
                    Log.w(TAG, "Found not working server! Send notification!");
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
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                .Builder(
                        ServerMonitorWorker.class,
                1,
                TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                workRequest
        );
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.d(TAG, "Worker stopped");
    }
}