package com.fl.ligueprofantasy.view;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fl.ligueprofantasy.R;
import com.fl.ligueprofantasy.controller.PlayerLab;
import com.fl.ligueprofantasy.model.Player;
import com.fl.ligueprofantasy.model.Team;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamCreateFragment extends Fragment {


    public TeamCreateFragment() {
        // Required empty public constructor
    }


    UserData userData = UserData.get();
    String teamName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_team_create, container, false);

        int team1Num, team2Num, team3Num, team4Num, team5Num,team6Num, team7Num, fresherNum, totalPlayerNum;
        team1Num = team2Num = team3Num = team4Num = team5Num = team6Num = team7Num = fresherNum = totalPlayerNum = 0;
        double remainingBudget = 160;

        Team team = ((TeamCreateActivity) getActivity()).getNewTeam();

        if (getArguments().getInt("playerID") != 0) { //check if a new player has been added from playerListFragment
            int playerId = getArguments().getInt("playerID");

            int playerNum =  getArguments().getInt("playerNum");
            team.setPlayerId(playerNum, playerId);
        }


        final CheckBox teamNameCheckBox = (CheckBox) v.findViewById(R.id.create_team_checkbox_name);
        final EditText teamNameEditText = (EditText) v.findViewById(R.id.create_team_name);
        if (getArguments().containsKey("teamName")) { //check if a new player has been added from playerListFragment
            teamName = getArguments().getString("teamName");
            teamNameEditText.setText(teamName);
            if (teamName != null) {
                if (teamName.length() < 4) {
                    teamNameCheckBox.setChecked(false);
                } else {
                    teamNameCheckBox.setChecked(true);
                }
            }
        }

        final PlayerLab playerLab = PlayerLab.get();
        for (int i = 1; i <= 16; i++) {
            Player player = playerLab.getPlayer(team.getFullSquadPlayerId(i));
            if (player != null) {
                totalPlayerNum++;

                int playerTeam = player.getTeam();
                switch (playerTeam) {
                    case 1: team1Num++;
                        break;
                    case 2: team2Num++;
                        break;
                    case 3: team3Num++;
                        break;
                    case 4: team4Num++;
                        break;
                    case 5: team5Num++;
                        break;
                    case 6: team6Num++;
                        break;
                    case 7: team7Num++;
                        break;
                }
                if (player.isFresher()) {
                    fresherNum++;
                }

                remainingBudget -= player.getPrice();


                String imageViewID = "create_team_player" + i + "_shirt";
                int resID = getResources().getIdentifier(imageViewID, "id", getActivity().getPackageName());
                ImageView playerImage = (ImageView) v.findViewById(resID);
                playerImage.setImageResource(player.getImage());

                String textViewNameID = "create_team_player" + i + "_name";
                resID = getResources().getIdentifier(textViewNameID, "id", getActivity().getPackageName());
                TextView playerName = (TextView) v.findViewById(resID);
                playerName.setText(player.getLastNameOneWord());

                String textViewPriceID = "create_team_player" + i + "_price";
                resID = getResources().getIdentifier(textViewPriceID, "id", getActivity().getPackageName());
                TextView playerPrice = (TextView) v.findViewById(resID);
                playerPrice.setText("DT" + player.getPrice() + "m");
            }
        }

        final CheckBox max3CheckBox = (CheckBox) v.findViewById(R.id.create_team_checkbox_max3_same_team);
        /*if (team1Num > 3 || team2Num > 3 || team3Num > 3 || team4Num > 3 || team5Num > 3 || team6Num > 3 || team7Num > 3) {
            max3CheckBox.setChecked(false);
        } else {
            max3CheckBox.setChecked(true);
        }*/

        final CheckBox min1CheckBox = (CheckBox) v.findViewById(R.id.create_team_checkbox_min1_every_team);
        if (team1Num > 0 && team2Num > 0 && team3Num > 0 ) {
            min1CheckBox.setChecked(true);
        } else {
            min1CheckBox.setChecked(true);
        }

        final CheckBox minFreshersCheckBox = (CheckBox) v.findViewById(R.id.create_team_checkbox_min_freshers);
        if (fresherNum >= 2) {
            minFreshersCheckBox.setChecked(true);
        } else {
            minFreshersCheckBox.setChecked(false);
        }

        teamNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                teamName = teamNameEditText.getText().toString();
                if (teamName.length() < 4) {
                    teamNameCheckBox.setChecked(false);
                } else {
                    teamNameCheckBox.setChecked(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final int totalPlayerNumFinal = totalPlayerNum; //lol is this the best to avoid final error inside inner class
        Button submitButton = (Button) v.findViewById(R.id.create_team_button_submit);
        final Team finalTeam = team; //again...
        final double price = 107 - remainingBudget;
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalPlayerNumFinal < 16) {
                    Toast.makeText(getActivity(), "You need to complete your team first!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String message = "";
                    if (!min1CheckBox.isChecked()) {
                        message += "You need at least one player from every team \n";
                    }
                    if (!minFreshersCheckBox.isChecked()) {
                        message += "You need at least 3 freshers in your team \n";
                    }
                    if (!max3CheckBox.isChecked()) {
                        message += "You can have at most 3 players from the same team \n";
                    }
                    if (!teamNameCheckBox.isChecked()) {
                        message += "Your team name must be at least 4 characters long \n";
                    }
                    if (price > 107) {
                        message += "You can't exceed the budget \n";
                    }
                    if (message.equals("")) {
                        finalTeam.setOwner(userData.getUsername());
                        finalTeam.setDefNum(3);
                        finalTeam.setMidNum(4);
                        finalTeam.setFwdNum(3);
                        finalTeam.setName(teamName);
                        userData.setTeam(finalTeam);
                        String[] input = new String[]{String.valueOf(userData.getUser_id()), teamName, userData.getUsername(), String.valueOf(price)};
                        addTeamToDB(concat(input, finalTeam.getAllIds()));
                    } else {
                        message = message.substring(0, message.length() - 1); //to get rid of last \n
                        Toast.makeText(getActivity(), message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        TextView budget = (TextView) v.findViewById(R.id.create_team_remaining_budget);
        budget.setText("DT" + remainingBudget + "m");

        for (int i = 1; i <= 16; i++) {
            String playerViewID = "create_team_player" + i;
            int resID = getResources().getIdentifier(playerViewID, "id", getActivity().getPackageName());
            final LinearLayout playerView = (LinearLayout) v.findViewById(resID);
            final int playerNum = i;
            playerView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Fragment fragment = new PlayerListFragment();
                    Bundle args = new Bundle();
                    args.putInt("playerNum",  playerNum);
                    args.putBoolean("teamCreate", true);
                    args.putString("teamName", teamName);
                    if (playerNum < 3) { //ensures only list of players on that position are shown
                        args.putString("positionFilter","GK");
                    } else if (playerNum < 8) {
                        args.putString("positionFilter","DEF");
                    } else if (playerNum < 13) {
                        args.putString("positionFilter","MID");
                    } else {
                        args.putString("positionFilter","FWD");
                    }

                    fragment.setArguments(args);
                    getFragmentManager().beginTransaction().
                            replace(R.id.flContent, fragment).addToBackStack(null).commit();
                }
            });
        }

        return v;
    }

    private void addTeamToDB(String... input) {

        AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... input) {
                String message = null;
                System.out.println(input[0]+"KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("user_id",input[0])
                        .add("name", input[1] )
                        .add("owner", input[2])
                        .add("def_num", String.valueOf(3))
                        .add("mid_num",String.valueOf(4))
                        .add("fwd_num", String.valueOf(3))
                        .add("goal_id",input[4])
                        .add("player1_id",input[5])
                        .add("player2_id",input[6])
                        .add("player3_id",input[7])
                        .add("player4_id",input[8])
                        .add("player5_id",input[9])
                        .add("player6_id",input[10])
                        .add("player7_id",input[11])
                        .add("player8_id",input[12])
                        .add("player9_id",input[13])
                        .add("player10_id",input[14])
                        .add("sub_goal_id",input[15])
                        .add("sub_player1_id",input[16])
                        .add("sub_player2_id",input[17])
                        .add("sub_player3_id",input[18])
                        .add("sub_player4_id",input[19])
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.43.63:4000/teams/add_team").post(formBody)
                        .build();
               /* Request request = new Request.Builder()
                        .url("https://union.ic.ac.uk/acc/football/android_connect/add_team.php?user_id=" +  Integer.parseInt(input[0]) +"&name=" + input[1]
                                + "&owner=" + input[2]+ "&price=" + Double.parseDouble(input[3]) + "&goal="+ Integer.parseInt(input[4])
                                + "&player1=" + Integer.parseInt(input[5]) + "&player2=" + Integer.parseInt(input[6])
                                + "&player3=" + Integer.parseInt(input[7]) + "&player4=" + Integer.parseInt(input[8])
                                + "&player5=" + Integer.parseInt(input[9]) + "&player6=" + Integer.parseInt(input[10])
                                + "&player7=" + Integer.parseInt(input[11]) + "&player8=" + Integer.parseInt(input[12])
                                + "&player9=" + Integer.parseInt(input[13]) + "&player10=" + Integer.parseInt(input[14])
                                + "&sub_goal=" + Integer.parseInt(input[15]) + "&sub1=" + Integer.parseInt(input[16])
                                + "&sub2=" + Integer.parseInt(input[17]) + "&sub3=" + Integer.parseInt(input[18])
                                + "&sub4=" + Integer.parseInt(input[19]))
                        .build();*/
                try {
                    Response response = client.newCall(request).execute();
                    String resp = response.body().string();
                    System.out.println(resp+"JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ");

                    logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                    OkHttpClient client1 = new OkHttpClient();
                    Request request1 = new Request.Builder()
                            .url("http://192.168.43.63:4000/teams/last_id")
                            .build();
                    Response response1 = client1.newCall(request1).execute();
                    String jsonArray = response1.body().string();
                    JSONArray array = new JSONArray(jsonArray);

                    JSONObject object2 = array.getJSONObject(0);

                    int team_id = object2.getInt("id");
                    System.out.println(object2.getInt("id")+"FooZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
                    userData.setTeam_id(team_id);
                    if (resp.equals("success")) {


                        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

                        OkHttpClient client2 = new OkHttpClient();

                        RequestBody formBody1 = new FormBody.Builder()
                                .add("team_id", String.valueOf(team_id))
                                .add("user_id", input[0] ).build();
                        System.out.println(team_id+"FoooormBooooooooooooooooooooooooooody");
                        Request request2 = new Request.Builder()
                                .url("http://192.168.43.63:4000/teams/affecteteamauuser").post(formBody1)
                                .build();
                        client2.newCall(request2).execute();


                       /* int team_id = object.getInt("@last_id_in_teams");
                        userData.setTeam_id(team_id); */
                        message = "correct";
                    } else {
                        message = "User does not exist";
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    message = "Could not connect to server";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return message;
            }

            @Override
            protected void onPostExecute(String message) {
                if (message.equals("correct")) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), message,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        asyncTask.execute(input);
    }

    public String[] concat(String[] a, String [] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] c= new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

}
