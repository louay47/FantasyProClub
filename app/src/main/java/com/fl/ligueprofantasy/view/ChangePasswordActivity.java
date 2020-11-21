package com.fl.ligueprofantasy.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fl.ligueprofantasy.R;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class ChangePasswordActivity extends AppCompatActivity {
    ProgressBar progressBar;
    Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        final String email = getIntent().getExtras().getString("email");

        final TextView passwordTextView = (TextView) findViewById(R.id.change_password);
        final TextView passwordRepeatTextView = (TextView) findViewById(R.id.change_password_repeat);

        changePasswordButton = (Button) findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = passwordTextView.getText().toString();
                final String passwordRepeat = passwordRepeatTextView.getText().toString();
                String message = "";
                if (password.length() < 8) {
                    message += "Enter a password that is at least 8 characters long\n";
                } else if (password.length() > 29) {
                    message += "Unfortunately your password is too long\n";
                } else if (!password.equals(passwordRepeat)) {
                    message += "passwords don't match\n";
                }
                if (message.equals("")) {
                    progressBar = (ProgressBar) findViewById(R.id.change_password_progress);
                    changePasswordDB(email, password);
                } else {
                    message = message.substring(0, message.length() - 1); //to get rid of last \n
                    Toast.makeText(getBaseContext(), message,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void changePasswordDB(String... input) {

        AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                changePasswordButton.setText("");
                changePasswordButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... input) {
                String message;

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://union.ic.ac.uk/acc/football/android_connect/change_password.php?email=" + input[0]
                                + "&password=" + input[1])
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    message = response.body().string();
                    if (message.equals("failure")) {
                        message = "Email doesn't exist";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    message = "Could not connect to server";
                }
                return message;
            }

            @Override
            protected void onPostExecute(String message) {
                if (message.equals("success")) {
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    //show text instead of progress
                    changePasswordButton.setText(R.string.email_passcode);
                    changePasswordButton.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(getBaseContext(), message,
                        Toast.LENGTH_SHORT).show();
            }
        };

        asyncTask.execute(input);
    }
}
