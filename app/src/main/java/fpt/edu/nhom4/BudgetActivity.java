package fpt.edu.nhom4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.edu.nhom4.Data.DBConnection;
import fpt.edu.nhom4.DAO.BudgetDAO;
import fpt.edu.nhom4.Entity.BudgetEntity;

// BudgetActivity manages budget operations within the UI.
public class BudgetActivity extends AppCompatActivity {
    private Button btnSave, btnUpdate, btnBack;
    private EditText edtAmount, edtNote, edtEndDate, edtStartDate;
    private Spinner spCategory;
    private BudgetEntity budgetEntity;
    private BudgetDAO budgetDao;
    private int userId;
    // ExecutorService to manage background threads.
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // onCreate is called when the activity is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Initialize UI components and database.
        initializeViews();
        initializeDatabase();
        // Load initial data if available.
        loadInitialData();

        // Set click listeners for buttons to handle user interactions.
        btnSave.setOnClickListener(v -> executeInThread(this::addBudgetData));
        btnUpdate.setOnClickListener(v -> executeInThread(this::updateBudgetData));
        btnBack.setOnClickListener(v -> navigateBack());
    }

    // initializeViews binds UI components and sets up the spinner.
    private void initializeViews() {
        // Binding views to the properties.
        spCategory = findViewById(R.id.spCate);
        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        btnSave = findViewById(R.id.btnSave);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnBack);

        // Setting up the spinner with the categories adapter.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    // initializeDatabase sets up the database connection and shared preferences.
    private void initializeDatabase() {
        // Getting the database connection.
        DBConnection dbConnection = DBConnection.getInitialDatabase(getApplicationContext());
        budgetDao = dbConnection.budgetDAO();
        // Getting user ID from shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userID", -1);
    }

    // loadInitialData checks for any passed data and loads it into the UI.
    private void loadInitialData() {
        Bundle bundle = getIntent().getExtras();

        budgetEntity = (BudgetEntity) bundle.get("dataStranfer");
        if (budgetEntity != null) {
            retrieveDataFromDBtoUI(budgetEntity.getId());
        } else {
            showToast("Please insert the initial data!");
        }
    }

    // executeInThread runs a given action on a separate thread.
    private void executeInThread(Runnable action) {
        new Thread(action).start();
    }

    // navigateBack finishes the activity and returns to the previous screen.
    private void navigateBack() {
        Intent intent = new Intent(this, HomeActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    // gatherBudgetData collects data from UI elements and creates a BudgetEntity.
    private BudgetEntity gatherBudgetData() {
        String category = spCategory.getSelectedItem().toString();
        String amountString = edtAmount.getText().toString();
        String startDate = edtStartDate.getText().toString();
        String endDate = edtEndDate.getText().toString();
        String note = edtNote.getText().toString();
        double amount = parseDoubleOrZero(amountString);

        return new BudgetEntity(category, userId, amount, note, startDate, endDate);
    }

    // parseDoubleOrZero attempts to parse a double, returning zero on failure.
    private double parseDoubleOrZero(String numberString) {
        try {
            return Double.parseDouble(numberString);
        } catch (NumberFormatException e) {
            showToast("Please enter a valid number for amount.");
            return 0;
        }
    }

    // validateBudgetData checks if the budget data is valid.
    private boolean validateBudgetData(BudgetEntity budget) {
        if (budget.getCategory().isEmpty() || budget.getAmount() <= 0 ||
                budget.getStartDate().isEmpty() || budget.getEndDate().isEmpty()) {
            showToast("Data should not be empty and amount should be greater than zero.");
            return false;
        }
        return true;
    }

    // retrieveDataFromDBtoUI fetches a budget from the database and updates the UI.
    private void retrieveDataFromDBtoUI(int id) {
        executorService.execute(() -> {
            budgetEntity = budgetDao.findByID(id);
            runOnUiThread(this::populateUIWithBudgetData);
        });
    }

    // populateUIWithBudgetData updates UI fields with budget data.
    private void populateUIWithBudgetData() {
        if (budgetEntity != null) {
            int position = ((ArrayAdapter) spCategory.getAdapter()).getPosition(budgetEntity.getCategory());
            spCategory.setSelection(position);
            edtAmount.setText(String.valueOf(budgetEntity.getAmount()));
            edtNote.setText(budgetEntity.getNote());
            edtStartDate.setText(budgetEntity.getStartDate());
            edtEndDate.setText(budgetEntity.getEndDate());
        }
    }

    // addBudgetData gathers data from UI, validates, and adds a new budget to the database.
    private void addBudgetData() {
        BudgetEntity budget = gatherBudgetData();
        if (validateBudgetData(budget)) {
            executeDbOperation(() -> budgetDao.addBugdet(budget), "Insert successful!", "Insert not successful!");
        }
    }

    // updateBudgetData gathers data from UI, validates, and updates an existing budget in the database.
    private void updateBudgetData() {
        BudgetEntity budget = gatherBudgetData();
        if (validateBudgetData(budget)) {
            budget.setId(budgetEntity.getId());
            executeDbOperation(() -> budgetDao.updateBudget(budget), "Update successful!", "Update not successful!");
        }
    }

    // executeDbOperation performs a database operation and displays a toast based on the outcome.
    private void executeDbOperation(Runnable dbOperation, String successMessage, String failureMessage) {
        executorService.execute(() -> {
            try {
                dbOperation.run();
                runOnUiThread(() -> showToast(successMessage));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> showToast(failureMessage));
            }
        });
    }

    // showToast displays a short message to the user.
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // onDestroy is called when the activity is being destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shut down the ExecutorService to release resources.
        executorService.shutdownNow();
    }
}
