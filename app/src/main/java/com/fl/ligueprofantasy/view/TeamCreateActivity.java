package com.fl.ligueprofantasy.view;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.fl.ligueprofantasy.model.Team;

public class TeamCreateActivity extends SingleFragmentActivity {

    private Team newTeam = new Team();

    @Override
    protected Fragment createFragment() {
        return new TeamCreateFragment();
    }

    @Override
    protected Bundle getBundle() {
        Bundle args = new Bundle();
        return args;
    }

    public void setNewTeam(Team newTeam) {
        this.newTeam = newTeam;
    }

    public Team getNewTeam() {
        return newTeam;
    }
}
