package fpt.edu.nhom4.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.nhom4.Entity.TransactionEntity;

@Dao
public interface TransDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(TransactionEntity transEntity);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(TransactionEntity transEntity);
    @Delete
    void delete(TransactionEntity transEntity);
    @Query("Select * from `transaction` where userID=:userid")
    List<TransactionEntity> getallTrans(int userid);
    @Query("SELECT * FROM `transaction` WHERE userId = :userId AND TransDate BETWEEN :startdate AND :enddate")
    List<TransactionEntity> getTransactionsForUserWithinDates(int userId, String startdate, String enddate);

}
