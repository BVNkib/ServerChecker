package ru.kb.lt.serverchecker.repository.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.kb.lt.serverchecker.model.Server;

@Dao
public interface ServerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Server server);

    @Update
    void update(Server server);

    @Delete
    void delete(Server server);

    @Query("SELECT * FROM Server ORDER BY name")
    LiveData<List<Server>> getAllServers();

    // Синхронный метод для получения всех серверов
    @Query("SELECT * FROM Server ORDER BY name")
    List<Server> getAllServersSync();

    @Query("SELECT * FROM Server WHERE id = :id")
    LiveData<Server> getServerById(int id);
}
