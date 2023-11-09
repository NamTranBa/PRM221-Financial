package fpt.edu.nhom4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.edu.nhom4.Data.DBConnection;
import fpt.edu.nhom4.DAO.UserDAO;
import fpt.edu.nhom4.Entity.UserEntity;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUserName, edtDOB, edtConfirmPass, edtPassword;
    private TextView txtLogin;
    private UserDAO userDataAccess;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeUI();
    }

    private void initializeUI() {
        edtUserName = findViewById(R.id.edtUserName);
        edtDOB = findViewById(R.id.edtDOB);
        edtConfirmPass = findViewById(R.id.edtConfirmPass);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnRegister = findViewById(R.id.buttonRegister);
        txtLogin = findViewById(R.id.txtHaveLogin);
        DBConnection dbConnection = DBConnection.getInitialDatabase(this);
        userDataAccess = dbConnection.createDataDAO();

        btnRegister.setOnClickListener(v -> registerUser());
        txtLogin.setOnClickListener(v -> backtoLogin());
    }

    private boolean validateInput() {
        String userName = edtUserName.getText().toString().trim();
        String dob = edtDOB.getText().toString().trim();
        String confirmPass = edtConfirmPass.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(confirmPass) || TextUtils.isEmpty(password)) {
            showToast("Data should not be empty");
            return false;
        } else if (!confirmPass.equals(password)) {
            showToast("Passwords do not match");
            return false;
        }
        return true;
    }

    private void backtoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerUser() {
        if (validateInput()) {
            String userName = edtUserName.getText().toString().trim();
            String dob = edtDOB.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            
            UserEntity newUser = new UserEntity(userName, password, dob, 0.0);
            executorService.execute(() -> {
                try {
                    userDataAccess.register(newUser);
                    showToastOnMainThread("Register Successful!");
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    showToastOnMainThread("Error during registration!");
                }
            });
        }
    }

    private void showToastOnMainThread(String message) {
        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
