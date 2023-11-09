package fpt.edu.nhom4.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.nhom4.Entity.BudgetEntity;

@Dao
public interface BudgetDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addBugdet(BudgetEntity budgetEntity);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBudget(BudgetEntity budgetEntity);
    @Delete
    void deleteBudget(BudgetEntity budgetEntity);
    @Query("SELECT * from budget where userID=:id AND isInCart = 0")
    public List<BudgetEntity> loadAllBudget(int id);
    @Query("SELECT * FROM budget WHERE ID=:id")
    public BudgetEntity findByID(int id);
    @Query("DELETE FROM budget WHERE ID=:id")
    public void deleteItemByIDs(int id);
}
