package fpt.edu.nhom4.Data;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import fpt.edu.nhom4.DAO.TransDAO;
import fpt.edu.nhom4.DAO.BudgetDAO;
import fpt.edu.nhom4.DAO.UserDAO;
import fpt.edu.nhom4.Entity.BudgetEntity;
import fpt.edu.nhom4.Entity.TransactionEntity;
import fpt.edu.nhom4.Entity.UserEntity;

@Database(entities = {UserEntity.class, TransactionEntity.class, BudgetEntity.class}, version = 5)
public abstract class DBConnection extends RoomDatabase {
    private static final String dbName = "Final";
    private static DBConnection INSTANCE;
    public abstract UserDAO createDataDAO();

    public abstract BudgetDAO budgetDAO();

    public abstract TransDAO transDAO();
    public static synchronized DBConnection getInitialDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, DBConnection.class, dbName).fallbackToDestructiveMigration().build();
        }
        return  INSTANCE;
    }

    public static void desTroyInstance() {
        INSTANCE = null;
    }
}
