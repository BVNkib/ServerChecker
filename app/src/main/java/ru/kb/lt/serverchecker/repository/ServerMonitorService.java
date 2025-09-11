package ru.kb.lt.serverchecker.repository;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ru.kb.lt.serverchecker.model.Server;
import ru.kb.lt.serverchecker.worker.NotificationUtils;
import ru.kb.lt.serverchecker.worker.ServersRepository;

public class ServerMonitorService extends Service {
    private static final String TAG = "ServerMonitorService";
    private static final int CHECK_INTERVAL_MINUTES = 10;

    private ScheduledExecutorService scheduler;
    private ServersRepository serversRepository;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Start monitoring service!");
        serversRepository = new ServersRepository(getApplication());
        NotificationUtils.createNotificationChannel(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startMonitoring();

        return START_STICKY;
    }

    private void startMonitoring() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(() -> {
            // Получаем все серверы из базы данных
            List<Server> servers = serversRepository.getAllServersSync();

            if (servers.isEmpty()) {
                Log.d(TAG, "Нет серверов для мониторинга");
                return;
            }

            // Проверяем каждый сервер
            for (Server server : servers) {
                if (!ServerChecker.checkServerAvailability(server)) {
                    NotificationUtils
                            .showServerDownNotification(getApplicationContext(), server.getName());
                }
            }
        }, 0, CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduler != null) {
            scheduler.shutdown();
        }
        Log.d(TAG, "Сервис мониторинга остановлен");
    }
}