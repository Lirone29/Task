package com.example.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task.database.AESCrypt;
import com.example.task.database.PostsDatabaseHelper;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String TAG_USER_ID = "UserId";
    EditText _loginText;
    EditText _passwordText;
    Button _loginButton;
    TextView _registerLink;

    int userID;
    String _login;
    PostsDatabaseHelper databaseHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("MyTasks");

        databaseHelper = PostsDatabaseHelper.getInstance(getApplicationContext());

        _loginButton = (Button) findViewById(R.id.loginID);
        _registerLink = (TextView) findViewById(R.id.registerViewID);
        _loginText = (EditText) findViewById(R.id.usernameID);
        _passwordText = (EditText) findViewById(R.id.passwordID);

        _loginButton.setEnabled(true);
        _registerLink.setEnabled(true);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(_loginText.getText().toString(), _passwordText.getText().toString());
            }
        });

        _registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public void login(String tmpLogin, String tmpPassword) {

        if (validate(tmpLogin, tmpPassword)) {
            Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_LONG).show();
            _login = tmpLogin;
            onLoginSuccess();
        } else onLoginFailed();
    }

    public boolean validate(String tmpLogin, String tmpPassword) {

        //login constrains
        if (tmpLogin.isEmpty() || tmpLogin.length() < 4 || tmpLogin.length() > 20) {
            _loginText.setError("At least 4 characters");
            return false;
        } else {
            _loginText.setError(null);
        }

        //password constrains
        if (tmpPassword.isEmpty() || tmpPassword.length() < 4 || tmpPassword.length() > 20) {
            _passwordText.setError("Between 4 and 20 alphanumeric characters");
            return false;
        } else {
            _passwordText.setError(null);
        }


        String passwordEncrypted = encrypt(tmpPassword);

        String password = databaseHelper.getPassword(tmpLogin);

        if (passwordEncrypted.equals(password)) return true;
        else {
            _loginText.setError("Password or login not match");
            return false;
        }
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        userID = databaseHelper.getUserId(_login);
        Intent intent = new Intent(getApplicationContext(), ListsActivity.class);
        intent.putExtra(TAG_USER_ID, String.valueOf(userID));
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
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