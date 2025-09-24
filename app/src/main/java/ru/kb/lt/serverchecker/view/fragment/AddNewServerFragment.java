package ru.kb.lt.serverchecker.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.kb.lt.serverchecker.databinding.FragmentAddNewServerBinding;
import ru.kb.lt.serverchecker.model.Server;
import ru.kb.lt.serverchecker.viewmodel.ServerViewModel;

public class AddNewServerFragment extends Fragment {
    public static final String TAG = "AddNewServerActivity";

    private ServerViewModel serverViewModel;
    private FragmentAddNewServerBinding binding;

    //region overdrive
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentAddNewServerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "Start AddNewServerActivity");
        initialise();
        setListeners();
    }
    //endregion

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

    public void finish() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                .commit();
    }
}
