package ru.kb.lt.serverchecker.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.kb.lt.serverchecker.model.Server;
import ru.kb.lt.serverchecker.worker.ServersRepository;

public class ServerViewModel extends AndroidViewModel {
    private final ServersRepository repository;
    private final LiveData<List<Server>> allServers;

    public ServerViewModel(Application application) {
        super(application);
        repository = new ServersRepository(application);
        allServers = repository.getAllServers();
    }

    public LiveData<List<Server>> getAllServers() {
        return allServers;
    }

    public void insert(Server server) {
        repository.insert(server);
    }

    public void update(Server server) {
        repository.update(server);
    }

    public void delete(Server server) {
        repository.delete(server);
    }
}
