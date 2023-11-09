package fpt.edu.nhom4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

import fpt.edu.nhom4.DAO.TransDAO;
import fpt.edu.nhom4.DAO.UserDAO;
import fpt.edu.nhom4.Data.DBConnection;
import fpt.edu.nhom4.Data.TransAdapter;
import fpt.edu.nhom4.Entity.BudgetEntity;
import fpt.edu.nhom4.Entity.TransactionEntity;
import fpt.edu.nhom4.Entity.UserEntity;
import fpt.edu.nhom4.Entity.TransItem;
import fpt.edu.nhom4.Notification.NotificationApp;

public class TransactionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TransAdapter adapter;
    private NotificationManagerCompat notificationManagerCompat;
    ImageView back;
    Button btnSave;
    TransDAO dao;
    UserDAO userDAO;
    DBConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        this.notificationManagerCompat = NotificationManagerCompat.from(this);
        dbConnection = DBConnection.getInitialDatabase(getApplicationContext());
        dao = dbConnection.transDAO();
        userDAO = dbConnection.createDataDAO();

        back = findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionActivity.this, HomeActivity.class);
                finish();
            }
        });
        btnSave = findViewById(R.id.btnSave);
        Date date;
        Calendar calendar = Calendar.getInstance();
        date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(date);
        ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(TransItem.getInstance().getCartItems().size());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BudgetEntity item : TransItem.getInstance().getCartItems()) {
                    TransactionEntity trans = new TransactionEntity();
                    String Category = item.getCategory();
                    double Amount = item.getAmount();
                    int userID = item.getUserID();
                    String note = item.getNote();

                    trans.setCategory(Category);
                    trans.setAmount(Amount);
                    trans.setUserID(userID);
                    trans.setNote(note);
                    trans.setTransDate(formattedDate);

                    databaseExecutor.execute(() -> {
                        try {
                            UserEntity localUser = userDAO.getuserID(userID);
                            dao.add(trans);
                            if (Category.equalsIgnoreCase("Spending")) {
                                localUser.setBalance(localUser.getBalance() - Amount);
                            } else if (Category.equalsIgnoreCase("Income")) {
                                localUser.setBalance(localUser.getBalance() + Amount);
                            }
                            userDAO.update(localUser);
                            sendOnChannel1(localUser.getBalance());
                        } finally {
                            latch.countDown();
                        }
                    });

                }
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                }
                databaseExecutor.shutdown();
                TransItem.getInstance().clearItems();
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);

                finish();
            }
        });
        recyclerView = findViewById(R.id.rcvTrans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransAdapter(TransItem.getInstance().getCartItems());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void sendOnChannel1(double amount) {
            String title = "You have a transaction";
                NumberFormat formatter = new DecimalFormat("###,###");
                String formattedNumber = formatter.format(amount);

        Notification notification = new NotificationCompat.Builder(this, NotificationApp.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText("Your Balance: " + formattedNumber + " VND")
                .setPriority(NotificationCompat .PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        int notificationId = 1;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.notificationManagerCompat.notify(notificationId, notification);

    }

}