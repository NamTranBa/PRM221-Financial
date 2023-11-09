package fpt.edu.nhom4;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fpt.edu.nhom4.DAO.TransDAO;
import fpt.edu.nhom4.DAO.UserDAO;
import fpt.edu.nhom4.Data.DBConnection;
import fpt.edu.nhom4.Data.DataAdapter;
import fpt.edu.nhom4.Data.RecyclerInterface;
import fpt.edu.nhom4.Entity.BudgetEntity;
import fpt.edu.nhom4.Entity.TransactionEntity;
import fpt.edu.nhom4.Entity.UserEntity;

public class HomeActivity extends AppCompatActivity {

    // Main components
    private DrawerLayout drawerLayout;
    private TextView txtWelcome, txtBalance;
    private ImageView imgTransaction;
    private Button btnAddBudget, btnHistory, btnMaps;
    private RecyclerView recyclerViewData;
    private DataAdapter dataAdapter;

    // Data handling
    private SharedPreferences sharedPreferences;
    private int userId;
    private UserEntity user;
    private DBConnection dbConnection;
    private UserDAO userDao;
    private TransDAO transDao;

    // Handlers and Executors
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Executor executor = Executors.newSingleThreadExecutor();

    // Activity launcher for result handling
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    recyclerViewData.setAdapter(dataAdapter);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize SharedPreferences and database connections
        initializePreferencesAndDatabase();

        // Set up UI components
        setupToolbar();
        setupRecyclerView();
        setupNavigationDrawer();
        setupButtons();

        // Retrieve and display user-specific data
        retrieveAndDisplayUserDetails();
    }

    private void initializePreferencesAndDatabase() {
        sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userID", -1);
        dbConnection = DBConnection.getInitialDatabase(getApplicationContext());
        userDao = dbConnection.createDataDAO();
        transDao = dbConnection.transDAO();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupRecyclerView() {
        recyclerViewData = findViewById(R.id.rcvView);
        recyclerViewData.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewData.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        dataAdapter = new DataAdapter(new ArrayList<>(), this::onClickBudget);
        recyclerViewData.setAdapter(dataAdapter);
    }

    private void setupNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_drawer);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                logout();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupButtons() {
        txtWelcome = findViewById(R.id.welcome);
        txtBalance = findViewById(R.id.txtBalance);
        imgTransaction = findViewById(R.id.imgTransaction);
        btnAddBudget = findViewById(R.id.btnBugdet);
        btnHistory = findViewById(R.id.btnHistory);
        btnMaps = findViewById(R.id.btnMap);
        // Set click listeners
        imgTransaction.setOnClickListener(view -> navigateToActivity(TransactionActivity.class));
        btnAddBudget.setOnClickListener(view -> onClickBudget(new BudgetEntity()));
        btnHistory.setOnClickListener(view -> navigateToActivity(HistoryActivity.class));
        btnMaps.setOnClickListener(view -> navigateToActivity(MapsActivity.class));
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // This clears all the data in shared preferences
        editor.apply();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // This will close the current activity and remove it from the activity stack
    }
    private void retrieveAndDisplayUserDetails() {
        String name = getIntent().getStringExtra("name");
        txtWelcome.setText("Welcome: " + name);
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(HomeActivity.this, activityClass);
        startActivity(intent);
    }

    private void onClickBudget(BudgetEntity data) {
        Intent intent = new Intent(this, BudgetActivity.class);
        intent.putExtra("dataStranfer", data);
        activityResultLauncher.launch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndUpdateBalance();
        updateChartData();
        refreshData();
    }

    private void refreshData() {
        executor.execute(() -> {
            List<BudgetEntity> dataList = dbConnection.budgetDAO().loadAllBudget(userId);
            mainHandler.post(() -> dataAdapter.setData(dataList));
        });
    }

    private void fetchAndUpdateBalance() {
        executor.execute(() -> {
            user = userDao.getuserID(userId);
            double balance = user.getBalance();
            mainHandler.post(() -> {
                NumberFormat formatter = new DecimalFormat("#,###");
                txtBalance.setText(formatter.format(balance) + " VND");
            });
        });
    }

    private void updateChartData() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<TransactionEntity> transactions = transDao.getallTrans(userId);

            HashMap<String, Float> categoryTotals = new HashMap<>();
            HashMap<String, Integer> categoryColors = new HashMap<>();

            for (TransactionEntity trans : transactions) {
                String category = trans.getCategory();
                double amount = trans.getAmount();
                categoryColors.put(category, trans.getCategoryColor());
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + (float) amount);
            }
            runOnUiThread(() -> {
                List<PieEntry> entries = new ArrayList<>();
                List<Integer> colors = new ArrayList<>();

                for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
                    String category = entry.getKey();
                    entries.add(new PieEntry(entry.getValue(), category));
                    int color = ContextCompat.getColor(getApplicationContext(), categoryColors.get(category));
                    colors.add(color);
                }
                PieDataSet dataSet = new PieDataSet(entries, "");
                dataSet.setColors(colors);
                PieData data = new PieData(dataSet);
                data.getDataSet().setValueTextSize(20f);
                PieChart pieChart = findViewById(R.id.pieChart);
                pieChart.setData(data);
                pieChart.setDrawEntryLabels(false);
                Description des = new Description();
                des.setText("Được tính bằng VND");
                pieChart.setDescription(des);
                pieChart.invalidate(); // Refresh chart
            });
        });
    }

}
