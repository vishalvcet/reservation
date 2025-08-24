package com.example.libraryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class LoginActivity extends Activity {

    EditText etUsername;
    RadioGroup rgRole;
    RadioButton rbUser, rbAdmin;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Linking XML IDs to Java
        etUsername = findViewById(R.id.etUsername);
        rgRole = findViewById(R.id.rgRole);
        rbUser = findViewById(R.id.rbUser);
        rbAdmin = findViewById(R.id.rbAdmin);
        btnLogin = findViewById(R.id.btnLogin);

        // Button Click Action
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rbAdmin.isChecked()) {
                startActivity(new Intent(this, AdminActivity.class));
            } else if (rbUser.isChecked()) {
                Intent i = new Intent(this, UserActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            } else {
                Toast.makeText(this, "Select role", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
