package io.xconn.excelfilereader.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    void insertAll(List<LocationEntity> locations);

    @Query("SELECT * FROM locations WHERE id = :id")
    LocationEntity getLocationById(String id);
}
