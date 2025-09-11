package ru.kb.lt.serverchecker.view.activivty;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.kb.lt.serverchecker.R;
import ru.kb.lt.serverchecker.databinding.ActivityMainBinding;
import ru.kb.lt.serverchecker.model.Server;
import ru.kb.lt.serverchecker.repository.ServerChecker;
import ru.kb.lt.serverchecker.backgrounds.ServerMonitorService;
import ru.kb.lt.serverchecker.view.custom.ServerAdapter;
import ru.kb.lt.serverchecker.viewmodel.ServerViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private List<Server> servers;
    private ServerViewModel serverViewModel;

    private ServerAdapter serverAdapter;
    private ServerAdapter.OnServerClickListener serverClickListener;

    private ActivityMainBinding binding;

    //region overdrive
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestPermissions();

        initialise();
        setListeners();
        setAdapters();
        setServersObserver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startProgress();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        stopProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopProgress();
    }
    //endregion

    //region initialise
    private void initialise() {
        serverViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
        servers = new ArrayList<>();
    }

    private void setListeners() {
        serverClickListener =
                new ServerAdapter.OnServerClickListener() {
                    @Override
                    public void onServerCheckClick(Server server) {
                        checkServer(server);
                    }

                    @Override
                    public void onServerDeleteClick(Server server) {
                        deleteServer(server);
                    }
                };
        binding.addButton.setOnClickListener( v-> addServer());
    }

    private void setAdapters() {
        serverAdapter = new ServerAdapter(servers, serverClickListener);
        binding.serverRecycler.setAdapter(serverAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.serverRecycler.setLayoutManager(linearLayoutManager);
    }

    private void setServersObserver() {
        serverViewModel.getAllServers().observe(this, servers -> {
            if (servers == null) return;
            checkAllServers(servers);
            stopProgress();
            startBackgroundChecks();
        });
    }
    //endregion

    //region progress
    private void startProgress() {
        binding.mainProgress.setVisibility(VISIBLE);
        binding.serverRecycler.setVisibility(GONE);
    }

    private void stopProgress() {
        binding.mainProgress.setVisibility(GONE);
        binding.serverRecycler.setVisibility(VISIBLE);
    }
    //endregion

    //region server_work
    private void addServer() {
        Intent intent = new Intent(getApplicationContext(), AddNewServerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void checkServer(Server server) {
        ServerChecker.checkServerAvailability(server, (isAvailable, serverName) -> {
            for (int id = 0; id < servers.size(); id++) {
                if (Objects.equals(servers.get(id).getName(), serverName)) {
                    server.setAvailability(isAvailable);
                    int finalId = id;
                    runOnUiThread(() -> serverAdapter.notifyItemChanged(finalId));
                    break;
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void checkAllServers(List<Server> servers) {
        ServerChecker.checkServersAvailability(servers, (checkedServers) -> {
            if (checkedServers == null) return;
            this.servers.clear();
            this.servers.addAll(checkedServers);
            runOnUiThread(() -> serverAdapter.notifyDataSetChanged());
            Log.d(TAG, "Servers checked! " + checkedServers.size() + " " + servers.size());
        });
    }

    private void deleteServer(Server server) {
        serverViewModel.delete(server);
    }
    //endregion

    private void startBackgroundChecks() {
        Intent serviceIntent = new Intent(this, ServerMonitorService.class);
        startService(serviceIntent);
//        ServerMonitorWorker.scheduleWork(getApplicationContext());
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
            }
        }
    }

}
