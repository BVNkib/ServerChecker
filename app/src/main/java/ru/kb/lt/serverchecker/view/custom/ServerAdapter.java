package ru.kb.lt.serverchecker.view.custom;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.kb.lt.serverchecker.databinding.ListServerItemBinding;
import ru.kb.lt.serverchecker.model.Server;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {
    private List<Server> serverList;
    private final OnServerClickListener listener;

    public interface OnServerClickListener {
        void onServerCheckClick(Server server);
        void onServerDeleteClick(Server server);
    }

    public ServerAdapter(List<Server> serverList, OnServerClickListener listener) {
        this.serverList = serverList;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setServerList(List<Server> serverList) {
        this.serverList = serverList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ServerViewHolder(ListServerItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {
        Server server = serverList.get(position);
        holder.bind(server, listener);
    }

    @Override
    public int getItemCount() {
        return serverList.size();
    }

    public static class ServerViewHolder extends RecyclerView.ViewHolder {
        private final ListServerItemBinding binding;

        public ServerViewHolder(ListServerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Server server, OnServerClickListener listener) {
            binding.serverNameHeader.setText(server.getName());
            binding.serverUrlHeader.setText(server.getUrl());

            binding.deleteButton.setOnClickListener(v -> listener.onServerDeleteClick(server));
            binding.checkButton.networkButtonCard.setOnClickListener(
                    v -> listener.onServerCheckClick(server));

            NetworkButton networkButton = new NetworkButton(
                    binding.checkButton, itemView.getContext());
            networkButton.setProgress();

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // pass
                }
                if (server.isAvailable()) {
                    networkButton.setGood();
                } else {
                    networkButton.setBad();
                }
            }).start();
        }
    }
}
