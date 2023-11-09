package fpt.edu.nhom4.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import fpt.edu.nhom4.R;

@Entity(tableName = "transaction")
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    public int Id;
    @ColumnInfo
    public String Category;
    @ColumnInfo
    public int userID;
    @ColumnInfo
    public double Amount;
    @ColumnInfo
    public String note;
    @ColumnInfo
    public String TransDate;

    public TransactionEntity() {
    }

    public TransactionEntity(int id, String category, int userID, double amount, String note, String transDate) {
        Id = id;
        Category = category;
        this.userID = userID;
        Amount = amount;
        this.note = note;
        TransDate = transDate;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTransDate() {
        return TransDate;
    }

    public void setTransDate(String transDate) {
        TransDate = transDate;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getCategoryColor() {
        switch (Category) {
            case "Spending":
                return R.color.category_one_color;
            case "Income":
                return R.color.category_two_color;
            // Add more cases as needed
            default:
                return R.color.black;
        }
    }
}
