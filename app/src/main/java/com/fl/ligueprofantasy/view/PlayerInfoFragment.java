package com.fl.ligueprofantasy.view;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fl.ligueprofantasy.R;
import com.fl.ligueprofantasy.controller.PlayerLab;
import com.fl.ligueprofantasy.model.Player;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerInfoFragment extends Fragment {


    public PlayerInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_player_info, container, false);
        int playerId = getArguments().getInt("playerID");

        PlayerLab playerLab = PlayerLab.get();
        final Player player = playerLab.getPlayer(playerId);

        ImageView mShirt = (ImageView) v.findViewById(R.id.player_info_image);
        mShirt.setImageResource(player.getImage());

        TextView mName = (TextView) v.findViewById(R.id.player_info_name);
        mName.setText(player.getFirstName() + " " + player.getLastName());

        TextView mPositionTeam = (TextView) v.findViewById(R.id.player_info_position_team);
        mPositionTeam.setText(player.getPosition() + " "+ player.getTeamString1());

        TextView mPoints = (TextView) v.findViewById(R.id.player_info_points);
        mPoints.setText(String.valueOf(player.getPoints()));

        TextView mPointsWeek = (TextView) v.findViewById(R.id.player_info_points_week);
        mPointsWeek.setText(String.valueOf(player.getPointsWeek()));

        TextView mApps = (TextView) v.findViewById(R.id.player_info_apps);
        mApps.setText(String.valueOf(player.getAppearances()));

        TextView mSubs = (TextView) v.findViewById(R.id.player_info_subs);
        mSubs.setText(String.valueOf(player.getSubAppearances()));

        TextView mGoals = (TextView) v.findViewById(R.id.player_info_goals);
        mGoals.setText(String.valueOf(player.getGoals()));

        TextView mAssits = (TextView) v.findViewById(R.id.player_info_assists);
        mAssits.setText(String.valueOf(player.getAssists()));

        TextView mCleanSheets= (TextView) v.findViewById(R.id.player_info_clean_sheets);
        mCleanSheets.setText(String.valueOf(player.getCleanSheets()));

        TextView mMotms = (TextView) v.findViewById(R.id.player_info_motms);
        mMotms.setText(String.valueOf(player.getMotms()));

        TextView mYellows = (TextView) v.findViewById(R.id.player_info_yellows);
        mYellows.setText(String.valueOf(player.getYellowCards()));

        TextView mReds = (TextView) v.findViewById(R.id.player_info_reds);
        mReds.setText(String.valueOf(player.getRedCards()));

        TextView mOwnGoals = (TextView) v.findViewById(R.id.player_info_own_goals);
        mOwnGoals.setText(String.valueOf(player.getOwnGoals()));

        return v;
    }

}
