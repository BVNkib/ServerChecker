package ru.kb.lt.serverchecker.view.activivty;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import ru.kb.lt.serverchecker.R;
import ru.kb.lt.serverchecker.backgrounds.ServerMonitorWorker;
import ru.kb.lt.serverchecker.databinding.ActivityMainBinding;
import ru.kb.lt.serverchecker.view.fragment.MainFragment;
import ru.kb.lt.serverchecker.viewmodel.ServerViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    //region overdrive
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.i(TAG, "MainActivity started!");

        startProgress();
        requestPermissions();
        initialise();

        startMainFragment();
        ServerMonitorWorker.scheduleWork(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        startProgress();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        ServerViewModel serverViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
        if (serverViewModel.getAllServers().getValue() != null) {
            Log.i(TAG, "Current servers size is: " +
                    serverViewModel.getAllServers().getValue().size());
        }
    }
    //endregion

    //region progress
    private void startProgress() {
        binding.mainProgress.setVisibility(VISIBLE);
    }

    private void stopProgress() {
        binding.mainProgress.setVisibility(GONE);
    }
    //endregion

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
            }
        }
    }

    private void startMainFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.fragmentContainer.getId(), new MainFragment())
                .addToBackStack(null)
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left)
                .commit();
    }
}
