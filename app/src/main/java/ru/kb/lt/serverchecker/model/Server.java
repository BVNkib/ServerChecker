package ru.kb.lt.serverchecker.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.net.MalformedURLException;
import java.net.URL;

@Entity
public class Server {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String url;
    private String name;
    @Ignore
    private boolean availability;

    public Server() {}

    @Ignore
    public Server(String url, String name) {
        this.url = url;
        this.name = name;
    }

    //region getters
    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return availability;
    }

    public URL getUrlUrl() throws MalformedURLException {
        return new URL(this.url);
    }
    //endregion

    //region setters
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
    //endregion
}
