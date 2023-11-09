package fpt.edu.nhom4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fpt.edu.nhom4.Data.DBConnection;
import fpt.edu.nhom4.DAO.UserDAO;
import fpt.edu.nhom4.Entity.UserEntity;

public class LoginActivity extends AppCompatActivity {
    private static final String SHARED_PREF_NAME = "MyApp";
    private static final String USER_ID_KEY = "userID";

    private SharedPreferences sharedPreferences;
    private EditText edtName, edtPassword;
    private Button btnLogin;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeDatabase();
        setupViews();
    }

    private void initializeDatabase() {
        DBConnection dbConnection = DBConnection.getInitialDatabase(this);
        userDAO = dbConnection.createDataDAO();
    }

    private void setupViews() {
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        TextView textViewRegister = findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(view -> startRegisterActivity());

        btnLogin = findViewById(R.id.buttonLogin);
        edtName = findViewById(R.id.edtUseName);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin.setOnClickListener(v -> attemptLogin(editor));
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void attemptLogin(SharedPreferences.Editor editor) {
        String name = edtName.getText().toString();
        String password = edtPassword.getText().toString();
        if (name.isEmpty() || password.isEmpty()) {
            showToast("Fill all fields");
            return;
        }

        new Thread(() -> performLogin(editor, name, password)).start();
    }

    private void performLogin(SharedPreferences.Editor editor, String name, String password) {
        UserEntity user = userDAO.login(name, password);
        if (user == null) {
            runOnUiThread(() -> showToast("Invalid Credentials"));
        } else {
            saveUserInfo(editor, user);
            navigateToHome(user);
        }
    }

    private void saveUserInfo(SharedPreferences.Editor editor, UserEntity user) {
        editor.putInt(USER_ID_KEY, user.Id);
        editor.apply();
    }

    private void navigateToHome(UserEntity user) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("balance", user.balance);
        intent.putExtra("name", user.userName);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
