package fpt.edu.nhom4.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int Id;
    @ColumnInfo
    public String userName;
    @ColumnInfo
    public String passWord;
    @ColumnInfo
    public String DoB;
    @ColumnInfo
    public double balance;

    public UserEntity() {
    }

    public UserEntity(String userName, String passWord, String doB, double balance) {
        this.userName = userName;
        this.passWord = passWord;
        DoB = doB;
        this.balance = balance;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getDoB() {
        return DoB;
    }

    public void setDoB(String doB) {
        DoB = doB;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
