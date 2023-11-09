package fpt.edu.nhom4.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import fpt.edu.nhom4.Entity.UserEntity;

@Dao
public interface UserDAO {
    @Insert
    void register(UserEntity userEntity);
    @Query("SELECT * from user where userName=(:name) and passWord=(:password)")
    UserEntity login(String name, String password);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(UserEntity userEntity);
    @Query("Select * from user where Id=:id")
    UserEntity getuserID(int id);
}
