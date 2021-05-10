package com.lazarski.cis4280finalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category WHERE id = :id")
    public Category getCategory(long id);

    @Query("SELECT * FROM Category WHERE text = :categoryText")
    public Category getCategoryByText(String categoryText);

    @Query("SELECT * FROM Category ORDER BY text")
    public List<Category> getCategories();

    @Query("SELECT * FROM Category ORDER BY updated DESC")
    public List<Category> getCategoriesNewerFirst();

    @Query("SELECT * FROM Category ORDER BY updated ASC")
    public List<Category> getCategoriesOlderFirst();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertCategory(Category category);

    @Update
    public void updateCategory(Category category);

    @Delete
    public void deleteCategory(Category category);

}
