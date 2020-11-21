package com.fl.ligueprofantasy.model;

public class UserData {
    private int user_id;
    private String username;
    private int team_id;
    private int adminedTeam;
    private boolean isSuperAdmin;
    private static UserData sUserData;
    Team mTeam;

    public void clearUserData() {
        sUserData = null;
    }

    public static UserData get() {
        if (sUserData == null) {
            sUserData   = new UserData();
        }
        return sUserData;
    }

    public static UserData get(int user_id, String username, int team_id, Team team, int adminedTeam, boolean isSuperAdmin) {
        if (sUserData == null) {
            sUserData   = new UserData(user_id, username, team_id, team, adminedTeam, isSuperAdmin);
        }
        return sUserData;
    }

    public static UserData get(int user_id, String username, int adminedTeam, boolean isSuperAdmin) {
        if (sUserData == null) {
            sUserData   = new UserData(user_id, username, adminedTeam, isSuperAdmin);
        }
        return sUserData;
    }
    private UserData() {
    }

    private UserData(int user_id, String username, int adminedTeam, boolean isSuperAdmin) {
        this.user_id = user_id;
        this.username = username;
        this.adminedTeam = adminedTeam;
        this.isSuperAdmin = isSuperAdmin;
    }

    private UserData(int user_id, String username, int team_id, Team team, int adminedTeam, boolean isSuperAdmin) {
        this.user_id = user_id;
        this.username = username;
        this.team_id = team_id;
        this.mTeam = team;
        this.adminedTeam = adminedTeam;
        this.isSuperAdmin = isSuperAdmin;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) { this.team_id = team_id;
    }

    public Team getTeam() {
        return mTeam;
    }

    public String getUsername() {
        return username;
    }

    public void setTeam(Team team) {
        this.mTeam = team;
    }

    public int getAdminedTeam() {
        return adminedTeam;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }
}
