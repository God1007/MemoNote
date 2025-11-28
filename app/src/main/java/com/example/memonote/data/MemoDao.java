package com.example.memonote.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MemoDao {
    @Query("SELECT * FROM memos ORDER BY updatedAt DESC")
    List<Memo> getAll();

    @Query("SELECT * FROM memos WHERE id = :id LIMIT 1")
    Memo getById(long id);

    @Query("SELECT * FROM memos WHERE (:category IS NULL OR :category = '' OR category = :category) AND (title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%') ORDER BY updatedAt DESC")
    List<Memo> search(String keyword, String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Memo memo);

    @Update
    void update(Memo memo);

    @Delete
    void delete(Memo memo);
}
