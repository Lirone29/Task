package com.example.task;

import android.content.Intent;
import android.os.Bundle;

import com.example.task.database.AESCrypt;
import com.example.task.database.PostsDatabaseHelper;
import com.example.task.models.Lists;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String TAG_USER_ID = "UserId";

    EditText _loginText;
    EditText _passwordText;
    Button _returnButton;
    Button _registerButton;
    String login;
    String password;
    PostsDatabaseHelper databaseHelper;

    int userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("MyTasks");

        // Get singleton instance of database
        databaseHelper = PostsDatabaseHelper.getInstance(getApplicationContext());

        _loginText = findViewById(R.id.registerLoginID);
        _passwordText = findViewById(R.id.registerPasswordID);
        _returnButton = findViewById(R.id.registerReturnToLoginID);
        _registerButton = findViewById(R.id.registerAccountID);

        _registerButton.setEnabled(true);
        _returnButton.setEnabled(true);

        _returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        _registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(_loginText.getText().toString(),_passwordText.getText().toString());
            }
        });
    }

    public boolean validate(String login, String password){

        //login constrains
        if (login.isEmpty() || login.length() < 4 || login.length() >20) {
            _loginText.setError("At least 4 characters");
            return false;
        } else {
            _loginText.setError(null);
        }

        //verifying login uniques in database
        if(databaseHelper.getUsername(login) != -1) return false;

        //password constrains
        if (password.isEmpty() || password.length() < 4 || password.length() > 20 ) {
            _passwordText.setError("Between 4 and 20 alphanumeric characters");
            return false;
        } else {
            _passwordText.setError(null);
        }
        return true;
    }

    public void signUp(String log, String pass){

        if (!validate(log,pass)) {
            onSignUpFailed();
        }

        createNewUser();
        onSignUpSuccess();
    }

    public void onSignUpSuccess() {
        Toast.makeText(getBaseContext(), "Registration was procedes succesfully", Toast.LENGTH_LONG).show();
        _returnButton.setEnabled(false);
        _registerButton.setEnabled(false);
        setResult(RESULT_OK, null);

        Intent intent = new Intent(getApplicationContext(), ListsActivity.class);
        intent.putExtra(TAG_USER_ID, String.valueOf(this.userId));
        startActivity(intent);
        finish();
    }

    public void onSignUpFailed() {
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();
        _returnButton.setEnabled(true);
        _registerButton.setEnabled(true);
    }

    public int createNewUser(){

        this.userId = databaseHelper.addUser(String.valueOf(_loginText.getText()), encrypt(String.valueOf(_passwordText.getText())));
        if(userId == -1) _loginText.setError("Can not create user in database!1!");

        return userId;
    }

    public String encrypt(String password) {
        try {
            return AESCrypt.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}