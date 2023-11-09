package fpt.edu.nhom4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.edu.nhom4.DAO.TransDAO;
import fpt.edu.nhom4.Data.DBConnection;
import fpt.edu.nhom4.Data.HistoryAdapter;
import fpt.edu.nhom4.Entity.TransactionEntity;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private TransDAO transactionDao;
    private Button btnStatistic;
    private ExecutorService executorService;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeDatabase();
        setupRecyclerView();

        setupBackButton();
        loadTransactions();
        btnStatistic = findViewById(R.id.btnStatistic);
        btnStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, StatisticActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Set up the database and DAO
    private void initializeDatabase() {
        DBConnection dbConnection = DBConnection.getInitialDatabase(getApplicationContext());
        transactionDao = dbConnection.transDAO();
    }

    // Configure the RecyclerView
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rcvTrans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Set up the back button with a click listener that finishes the activity
    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finishActivityWithResult());
    }



    // Load transactions from the database in the background and update the UI
    private void loadTransactions() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userID", -1);
            List<TransactionEntity> transactions = transactionDao.getallTrans(userId);
            mainHandler.post(() -> updateUIWithTransactions(transactions));
        });
    }

    // Update UI with the list of transactions
    private void updateUIWithTransactions(List<TransactionEntity> transactions) {
        historyAdapter = new HistoryAdapter(transactions);
        recyclerView.setAdapter(historyAdapter);
    }

    // Finish the activity and return to the previous screen with a result
    private void finishActivityWithResult() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
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
