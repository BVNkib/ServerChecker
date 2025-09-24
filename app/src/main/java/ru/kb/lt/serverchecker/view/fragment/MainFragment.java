package ru.kb.lt.serverchecker.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.kb.lt.serverchecker.R;
import ru.kb.lt.serverchecker.databinding.FragmentMainBinding;
import ru.kb.lt.serverchecker.model.Server;
import ru.kb.lt.serverchecker.repository.ServerChecker;
import ru.kb.lt.serverchecker.view.custom.ServerAdapter;
import ru.kb.lt.serverchecker.viewmodel.ServerViewModel;

public class MainFragment extends Fragment {
    private static final String TAG = "MainActivity";

    private List<Server> servers;
    private ServerViewModel serverViewModel;

    private ServerAdapter serverAdapter;
    private ServerAdapter.OnServerClickListener serverClickListener;

    private FragmentMainBinding binding;
    
    //region overdrive
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise();
        setListeners();
        setAdapters();
        setServersObserver();
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.serverRecycler.setLayoutManager(linearLayoutManager);
    }

    private void setServersObserver() {
        serverViewModel.getAllServers().observe(getViewLifecycleOwner(), servers -> {
            if (servers == null) return;
            updateServersUi(servers);
            checkAllServers(servers);
        });
    }
    //endregion

    //region server_work
    private void addServer() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .attach(new AddNewServerFragment())
                .detach(this)
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left)
                .commit();

    }

    private void checkServer(Server server) {
        ServerChecker.checkServerAvailability(server, (isAvailable, serverName) -> {
            for (int id = 0; id < servers.size(); id++) {
                if (Objects.equals(servers.get(id).getName(), serverName)) {
                    server.setAvailability(isAvailable);
                    int finalId = id;
                    requireActivity().runOnUiThread(() -> serverAdapter.notifyItemChanged(finalId));
                    break;
                }
            }
        });
    }

    private void checkAllServers(List<Server> servers) {
        ServerChecker.checkServersAvailability(servers, (checkedServers) -> {
            if (checkedServers == null) return;
            updateServersUi(checkedServers);
            Log.d(TAG, "Servers checked! " + checkedServers.size() + " " + servers.size());
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateServersUi(List<Server> newServers) {
        this.servers.clear();
        this.servers.addAll(newServers);
        requireActivity().runOnUiThread(() -> serverAdapter.notifyDataSetChanged());
    }

    private void deleteServer(Server server) {
        serverViewModel.delete(server);
    }
    //endregion
}

