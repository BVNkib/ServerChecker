package ru.kb.lt.serverchecker.view.activivty;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ru.kb.lt.serverchecker.databinding.ActivityAddNewServerBinding;
import ru.kb.lt.serverchecker.model.Server;
import ru.kb.lt.serverchecker.viewmodel.ServerViewModel;

public class AddNewServerActivity extends AppCompatActivity {
    private static final String TAG = "AddNewServerActivity";

    private ServerViewModel serverViewModel;
    private ActivityAddNewServerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        binding = ActivityAddNewServerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.i(TAG, "Start AddNewServerActivity");

        initialise();
        setListeners();

        super.onCreate(savedInstanceState);
    }

    private void initialise() {
        serverViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
    }

    private void setListeners() {
        binding.addServer.setOnClickListener(v -> addServer());
        binding.backButton.setOnClickListener(v -> finish());
    }

    private void addServer() {
        String name = String.valueOf(binding.serverNameText.getText());
        String url = String.valueOf(binding.serverAddressText.getText());

        if (name.isEmpty()) {
            binding.serverName.setError("Input server name");
        } else if (url.isEmpty()) {
            binding.serverAddress.setError("Input server url");
        } else {
            Log.i(TAG, "Inserting new server!");
            serverViewModel.insert(new Server(url, name));
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }
}
