package com.fl.ligueprofantasy.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fl.ligueprofantasy.R;
import com.fl.ligueprofantasy.controller.PlayerLab;
import com.fl.ligueprofantasy.model.AppState;
import com.fl.ligueprofantasy.model.Constants;
import com.fl.ligueprofantasy.model.Team;
import com.fl.ligueprofantasy.model.User;
import com.fl.ligueprofantasy.model.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class LoginActivity extends AppCompatActivity {

    User user;
    Team team;
    String username;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);


         username = sharedPref.getString("username", null);
        if (username != null) { //if the user is logged in
            setContentView(R.layout.loading);
            getUserNoPassFromDB(username);
        } else {
            setContentView(R.layout.activity_login);
            final Button registerButton = (Button) findViewById(R.id.login_register);
            final ScrollView activityRootView = (ScrollView) findViewById(R.id.login_scroll_view);
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                    if (heightDiff > dpToPx(getApplicationContext(), 200)) { // if more than 200 dp, it's probably a keyboard...
                        activityRootView.smoothScrollTo(0, registerButton.getTop());
                    }
                }
            });

            final EditText usernameEditText = (EditText) findViewById(R.id.login_username);
            final EditText passwordEditText = (EditText) findViewById(R.id.login_password);

            final Button signInButton = (Button) findViewById(R.id.login_sign_in);
            signInButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                      username = usernameEditText.getText().toString();
                      password = passwordEditText.getText().toString();

                    String message = "You need to enter";
                    if (username.equals("")) {
                        message += " your username";
                    }
                    if (password.equals("")) {
                        if (message.equals("You need to enter your username")) {
                            message += " and";
                        }
                        message += " your password";
                    }
                    if (message.equals("You need to enter")) { //then all fields filled
                        signInButton.setText("");
                        signInButton.setEnabled(false);
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.login_progress);
                        progressBar.setVisibility(View.VISIBLE);
                        getUserFromDB(username, password);
                    } else {
                        Toast.makeText(getBaseContext(), message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });


            registerButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                }
            });

            TextView forgotPassword = (TextView) findViewById(R.id.login_forgot_password);
            forgotPassword.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    private void getUserFromDB(String... data) {

        AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... data) {
                String message;

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("name", username)
                        .add("password",password)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.63:4000/users/login").post(formBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                  //  JSONArray jsonArray = response.body().string();
                   // String jsonArray = (response.body().string());
                    JSONObject object = new JSONObject(response.body().string());

                   if (!object.equals("\"not found\"")) {
                       // JSONArray array = new JSONArray(jsonArray);
                        //JSONArray array = new JSONArray(response.body().string());

                       // JSONObject object = jsonArray.getJSONObject();

                        user = new User(object.getInt("id"), 0, data[0],
                                object.getInt("id"), false);
                        message = "correct";

                    } else {
                        message = "Invalid username or password";
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    message = "Could not connect to server";
                } catch (JSONException e) {
                    e.printStackTrace();
                    message = "invalid Username or password";
                }
                return message;
            }

            @Override
            protected void onPostExecute(String message) {
                PlayerLab.get(); //gets players from database
                if (message.equals("correct")) {
                    SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("username", user.getUsername());

                    editor.apply();
                    getStateDB();
                } else {
                    Toast.makeText(getBaseContext(), message,
                            Toast.LENGTH_SHORT).show();
                    Button signInButton = (Button) findViewById(R.id.login_sign_in);
                    signInButton.setText("Sign In");
                    signInButton.setEnabled(true);
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.login_progress);
                    progressBar.setVisibility(View.GONE);
                }
            }
        };

        asyncTask.execute(data);
    }

    private void getUserNoPassFromDB(String... data) {

        AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... data) {
                String message;
                SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("name", username)

                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.63:4000/users/login_2").post(formBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                   /* String jsonArray = response.body().string();
                    JSONArray array = new JSONArray(jsonArray);*/

                    JSONObject object = new JSONObject(response.body().string());

                        user = new User(object.getInt("id"), object.getInt("team_id"), data[0],
                                0, false);


                    message = "correct";


                } catch (IOException e) {
                    e.printStackTrace();
                    message = "Could not connect to server";
                } catch (JSONException e) {
                    e.printStackTrace();


                    editor.remove("username");
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    message = "You have no team, please login to create one";
                }
                return message;
            }

            @Override
            protected void onPostExecute(String message) {
                PlayerLab.get(); //gets players from database
                if (message.equals("correct")) {
                    getStateDB();
                } else {
                    Toast.makeText(getBaseContext(), message,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        asyncTask.execute(data);
    }

    private void getStateDB(Integer... team_ids) {

        AsyncTask<Integer, Void, String> asyncTask = new AsyncTask<Integer, Void, String>() {
            @Override
            protected String doInBackground(Integer... team_ids) {
                String message;


                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://union.ic.ac.uk/acc/football/android_connect/get_state.php")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String jsonArray = response.body().string();
                    JSONArray array = new JSONArray(jsonArray);

                    JSONObject object = array.getJSONObject(0);

                    AppState.get(object.getInt("is_editable") == 1, object.getInt("is_transfer") == 1,
                            object.getString("next_editable"), object.getString("save_by"));
                    message = "correct";

                } catch (IOException e) {
                    e.printStackTrace();
                    message = "Could not connect to server";
                } catch (JSONException e) {
                    e.printStackTrace();
                    message = "Could not connect to server";

                }
                return message;
            }

            @Override
            protected void onPostExecute(String message) {
                if (message.equals("correct")) {
                    getTeamFromDB(user.getTeam_id());
                    System.out.println("Im heeeeeeeeeeeeeeeeeeeeeeeere");
                } else {
                    Toast.makeText(getBaseContext(), message,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        asyncTask.execute(team_ids);
    }

    private void getTeamFromDB(Integer... team_ids) {

        AsyncTask<Integer, Void, String> asyncTask = new AsyncTask<Integer, Void, String>() {
            @Override
            protected String doInBackground(Integer... team_ids) {
                String message;

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("owner", username).build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.63:4000/teams/serchteambyuser").post(formBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String jsonArray = response.body().string();
                    System.out.println(jsonArray.length()+"Jsoooooooooooooooooooooooonnnnnnn");
                    if (!(jsonArray.length()==2)) {
                        JSONArray array = new JSONArray(jsonArray);

                        JSONObject object = array.getJSONObject(0);

                        team = new Team(team_ids[0], object.getString("name"),
                                object.getString("owner"), object.getDouble("price"),
                                object.getInt("points"), object.getInt("points_week"),
                                object.getInt("def_num"), object.getInt("mid_num"),
                                object.getInt("fwd_num"), object.getInt("goal_id"),
                                object.getInt("player1_id"), object.getInt("player2_id"),
                                object.getInt("player3_id"), object.getInt("player4_id"),
                                object.getInt("player5_id"), object.getInt("player6_id"),
                                object.getInt("player7_id"), object.getInt("player8_id"),
                                object.getInt("player9_id"), object.getInt("player10_id"),
                                object.getInt("sub_goal_id"), object.getInt("sub_player1_id"),
                                object.getInt("sub_player2_id"), object.getInt("sub_player3_id"),
                                object.getInt("sub_player4_id"));
                        message = "correct";
                    } else {
                        message = "Team does not exist";
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    message = "Could not connect to server";
                } catch (JSONException e) {
                    e.printStackTrace();
                    message = "Could not connect to server";
                }
                return message;
            }

            @Override
            protected void onPostExecute(String message) {
                if (message.equals("Team does not exist")) {
                    UserData.get(user.getUser_id(), user.getUsername(), user.adminedTeam(), user.is_super_admin());
                    launchCreateTeamActivity();
                } else if (message.equals("correct")) {
                    UserData.get(user.getUser_id(), user.getUsername(), user.getTeam_id(), team, user.adminedTeam(), user.is_super_admin());
                    nextActivity();
                } else {
                    Toast.makeText(getBaseContext(), message,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        asyncTask.execute(team_ids);
    }


    private void nextActivity()
    { //sets the user_id and team_id in userData class
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void launchCreateTeamActivity()
    { //sets the user_id and team_id in userData class
        Intent intent = new Intent(getApplicationContext(), TeamCreateActivity.class);
        intent.putExtra("user_id", user.getUser_id());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

}
