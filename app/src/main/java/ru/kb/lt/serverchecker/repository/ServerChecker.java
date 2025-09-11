package ru.kb.lt.serverchecker.repository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import ru.kb.lt.serverchecker.model.Server;

public class ServerChecker {
    public interface CheckServerCallback {
        void onResult(boolean isAvailable, String serverName);
    }
    public interface CheckServersCallback {
        void onResult(List<Server> servers);
    }

    public static void checkServersAvailability(List<Server> servers, CheckServersCallback callback) {
        new Thread(() -> {
            if (callback == null) return;
            if (servers == null) return;
            for (Server server: servers) {
                server.setAvailability(checkServerAvailability(server));
            }
            callback.onResult(servers);
        }).start();
    }

    public static void checkServerAvailability(Server server, CheckServerCallback callback) {
        new Thread(() -> {
            if (callback == null) return;
            callback.onResult(checkServerAvailability(server), server.getName());
        }).start();
    }

    public static boolean checkServerAvailability(Server server) {
        boolean isAvailable;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) server.getUrlUrl().openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("HEAD");

            int responseCode = connection.getResponseCode();
            isAvailable = (responseCode >= 200 && responseCode < 400);
        } catch (IOException e) {
            isAvailable = false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return isAvailable;
    }
}