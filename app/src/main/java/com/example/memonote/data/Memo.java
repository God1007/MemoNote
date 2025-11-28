package com.example.memonote.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "memos")
public class Memo {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public String content;
    public String category;
    public long createdAt;
    public long updatedAt;
    public String imagePath;

    public Memo(String title, String content, String category, long createdAt, long updatedAt, String imagePath) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imagePath = imagePath;
    }
}
