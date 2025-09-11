package ru.kb.lt.serverchecker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ServerViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;

    public ServerViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel > T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ServerViewModel.class)) {
            return (T) new ServerViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
