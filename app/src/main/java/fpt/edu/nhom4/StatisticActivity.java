package fpt.edu.nhom4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.edu.nhom4.DAO.TransDAO;
import fpt.edu.nhom4.Data.DBConnection;
import fpt.edu.nhom4.Data.HistoryAdapter;
import fpt.edu.nhom4.Data.TransAdapter;
import fpt.edu.nhom4.Entity.TransactionEntity;

public class StatisticActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private TransDAO transactionDao;
    private ExecutorService executorService;
    Button btn;
    ImageView btnBack;
    TextView txtSpending, txtIncome;
    private DatePicker startDatePicker, endDatePicker;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        startDatePicker = findViewById(R.id.startDatePicker);
        endDatePicker = findViewById(R.id.endDatePicker);
        txtSpending = findViewById(R.id.tvSpendingData);
        txtIncome = findViewById(R.id.tvIncomeData);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back2history();
            }
        });
        initializeDatabase();
        setupRecyclerView();
        btn = findViewById(R.id.btnFetchData);
        ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseExecutor.execute(() ->{
                    loadTransactions();
                });

            }
        });


    }

    private void back2history() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
        finish();
    }

    // Set up the database and DAO
    private void initializeDatabase() {
        DBConnection dbConnection = DBConnection.getInitialDatabase(getApplicationContext());
        transactionDao = dbConnection.transDAO();
    }

    // Configure the RecyclerView
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rvDailyStats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    // Load transactions from the database in the background and update the UI
    private void loadTransactions() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        TransactionEntity trans1 = new TransactionEntity();
        // Get the start and end date on the UI thread
        int startYear = startDatePicker.getYear();
        int startMonth = startDatePicker.getMonth();
        int startDay = startDatePicker.getDayOfMonth();
        int endYear = endDatePicker.getYear();
        int endMonth = endDatePicker.getMonth();
        int endDay = endDatePicker.getDayOfMonth();

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(startYear, startMonth, startDay, 0, 0, 0);
            String selectedStartDate = dateFormat.format(startCalendar.getTime());

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(endYear, endMonth, endDay, 23, 59, 59);
            String selectedEndDate = dateFormat.format(endCalendar.getTime());

            SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userID", -1);

            List<TransactionEntity> transactions = transactionDao.getTransactionsForUserWithinDates(userId, selectedStartDate, selectedEndDate);
            double spending = 0;
            double income = 0;
            for (TransactionEntity trans : transactions) {
                if (trans.Category.equalsIgnoreCase("Spending")) {
                    spending += trans.Amount;
                } else {
                    income += trans.Amount;
                }
            }

            // Format the numbers
            NumberFormat formatter = new DecimalFormat("#,###");
            String formattedSpending = formatter.format(spending);
            String formattedIncome = formatter.format(income);

            // Switch to the main thread to update the UI
            runOnUiThread(() -> {
                txtSpending.setText(formattedSpending + " VND");
                txtIncome.setText(formattedIncome + " VND");
                updateUIWithTransactions(transactions); // this method must also only update the UI
            });
        });
    }



    // Update UI with the list of transactions
    private void updateUIWithTransactions(List<TransactionEntity> transactions) {
        historyAdapter = new HistoryAdapter(transactions);
        recyclerView.setAdapter(historyAdapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown the ExecutorService to free up resources
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}

