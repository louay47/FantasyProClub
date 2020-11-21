package com.fl.ligueprofantasy.view;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fl.ligueprofantasy.R;
import com.fl.ligueprofantasy.controller.PlayerLab;
import com.fl.ligueprofantasy.model.Player;
import com.fl.ligueprofantasy.model.Team;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamListFragment extends Fragment {


    public TeamListFragment() {
        // Required empty public constructor
    }


    private RecyclerView mTeamRecycleView;
    private TeamAdapter mAdapter;
    private LinearLayoutManager linearLayout;
    private List<Team> teams;
    private View view;
    PlayerLab playerLab;
    Player mPplayer = new Player();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_team_list, container, false);


        teams = new ArrayList<>();
        mTeamRecycleView = (RecyclerView) view.findViewById(R.id.team_recycler_view);
        linearLayout = new LinearLayoutManager(getContext());
        mTeamRecycleView.setLayoutManager(linearLayout);
        getTeamsFromDB();

        updateUI();

        LinearLayout headers = (LinearLayout) view.findViewById(R.id.team_list_headers);
        for (int i = 0; i < headers.getChildCount(); i++) {
            RelativeLayout header = (RelativeLayout) headers.getChildAt(i);
            header.setOnClickListener(mOnClickListener);
        }

        return view;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RelativeLayout relV = (RelativeLayout) v;
            hideAllCheckboxes();
            CheckBox checkBox = (CheckBox) relV.getChildAt(1);
            checkBox.toggle();
            checkBox.setVisibility(View.VISIBLE);
            switch(v.getId()) {
                case R.id.team_list_name_layout:
                    if (checkBox.isChecked()) {
                        Collections.sort(teams, new Comparator<Team>() {
                            @Override
                            public int compare(Team o1, Team o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                    } else {
                        Collections.sort(teams, new Comparator<Team>() {
                            @Override
                            public int compare(Team o1, Team o2) {
                                return o2.getName().compareTo(o1.getName());
                            }
                        });
                    }
                    break;
                case R.id.team_list_owner_layout:
                    if (checkBox.isChecked()) {
                        Collections.sort(teams, new Comparator<Team>() {
                            @Override
                            public int compare(Team o1, Team o2) {
                                return o1.getOwner().compareTo(o2.getOwner());
                            }
                        });
                    } else {
                        Collections.sort(teams, new Comparator<Team>() {
                            @Override
                            public int compare(Team o1, Team o2) {
                                return o2.getOwner().compareTo(o1.getOwner());
                            }
                        });
                    }
                    break;
                case R.id.team_list_value_layout:
                    if (checkBox.isChecked()) {
                        Collections.sort(teams, new Comparator<Team>() {
                            @Override
                            public int compare(Team o1, Team o2) {
                                return Double.compare(o1.getPrice(), o2.getPrice());
                            }
                        });
                    } else {
                        Collections.sort(teams, new Comparator<Team>() {
                            @Override
                            public int compare(Team o1, Team o2) {
                                return Double.compare(o2.getPrice(), o1.getPrice());
                            }
                        });
                    }
                    break;
                case R.id.team_list_points_layout:
                    if (checkBox.isChecked()) {
                        Collections.sort(teams, new Comparator<Team>() {
                            @Override
                            public int compare(Team o1, Team o2) {
                                return Double.compare(o1.getPoints(), o2.getPoints());
                            }
                        });
                    } else {
                        Collections.sort(teams, new Comparator<Team>() {
                            @Override
                            public int compare(Team o1, Team o2) {
                                return Double.compare(o2.getPoints(), o1.getPoints());
                            }
                        });
                    }
                    break;
            }
            if (checkBox.isChecked()) {
                checkBox.setButtonDrawable(R.drawable.ic_keyboard_arrow_up);
            } else {
                checkBox.setButtonDrawable(R.drawable.ic_keyboard_arrow_down);
            }
            mAdapter.notifyDataSetChanged();
        }
    };


    private void updateUI() {

        mAdapter = new TeamAdapter(teams);
        mTeamRecycleView.setAdapter(mAdapter);
    }

    private class TeamHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mOwner;
        public TextView mPrice;
        public TextView mPoints;
        public Team mTeam;


        public TeamHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Fragment fragment = new TeamDisplayFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("teamList", true);
                    fragment.setArguments(args);
                    ((MainActivity) getActivity()).setCurrentTeamShown(mTeam);
                    fragment.setArguments(args);
                    getFragmentManager().beginTransaction().
                            replace(R.id.flContent, fragment).addToBackStack(null).commit();
                }
            });

            mName = (TextView) itemView.findViewById(R.id.list_team_name);
            mOwner = (TextView) itemView.findViewById(R.id.list_team_owner);
            mPrice = (TextView) itemView.findViewById(R.id.list_team_price);
            mPoints = (TextView) itemView.findViewById(R.id.list_team_points);
        }
    }

    private class TeamAdapter extends RecyclerView.Adapter<TeamHolder> {
        private List<Team> mTeams;
        public TeamAdapter(List<Team> teams) {
            mTeams = teams;
        }

        @Override
        public TeamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.team_row, parent, false);
            return new TeamHolder(view);
        }
        @Override
        public void onBindViewHolder(TeamHolder holder, int position) {
            Team team = mTeams.get(position);
            holder.mTeam = team;
            holder.mName.setText(team.getName());
            holder.mOwner.setText(team.getOwner());
            holder.mPrice.setText("DT" + team.getPrice() + " Mil");
            holder.mPoints.setText(String.valueOf(team.getPoints()));
        }

        @Override
        public int getItemCount() {
            return mTeams.size();
        }

    }



    private void getTeamsFromDB() {

        AsyncTask<String, Void, Void> asyncTask = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... sort) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://192.168.43.63:4000/teams/select_all")
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);
                        Team team = new Team(object.getInt("id"), object.getString("name"),
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


                        /*
                        playerLab = PlayerLab.get();
                        int PointsTeam =0 ;
                        Player goal =  playerLab.getPlayer(team.getGoalId());
                        PointsTeam += goal.getPoints();
                        Player player1 =  playerLab.getPlayer(team.get());
                        PointsTeam += goal.getPoints();
                        Player goal =  playerLab.getPlayer(team.getGoalId());
                        PointsTeam += goal.getPoints();
                        Player goal =  playerLab.getPlayer(team.getGoalId());
                        PointsTeam += goal.getPoints();
                        Player goal =  playerLab.getPlayer(team.getGoalId());
                        PointsTeam += goal.getPoints();
                        Player goal =  playerLab.getPlayer(team.getGoalId());
                        PointsTeam += goal.getPoints();
                        Player goal =  playerLab.getPlayer(team.getGoalId());
                        PointsTeam += goal.getPoints();
                        Player goal =  playerLab.getPlayer(team.getGoalId());
                        PointsTeam += goal.getPoints();
                        Player goal =  playerLab.getPlayer(team.getGoalId());
                        PointsTeam += goal.getPoints();
                        Player goal =  playerLab.getPlayer(team.getGoalId());
                        PointsTeam += goal.getPoints(); */
                        playerLab = PlayerLab.get();
                        String Ids[] = team.getAllIds();
                        int pointsTeam =0 ;

                        for (int j =0 ; j<Ids.length ; j++){

                            pointsTeam += playerLab.getPlayer(Integer.parseInt(Ids[j])).getPoints();

                        }
                        team.setPoints(pointsTeam);
                        teams.add(team);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
            }
        };

        asyncTask.execute();
    }

    private void hideAllCheckboxes() {
        LinearLayout headers = (LinearLayout) view.findViewById(R.id.team_list_headers);
        for (int i = 0; i < headers.getChildCount(); i++) {
            RelativeLayout header = (RelativeLayout) headers.getChildAt(i);
            for (int j = 0; j < header.getChildCount(); j++) {
                View v = header.getChildAt(j);
                if (v instanceof CheckBox) {
                    v.setVisibility(View.GONE);
                }
            }
        }

    }

}
