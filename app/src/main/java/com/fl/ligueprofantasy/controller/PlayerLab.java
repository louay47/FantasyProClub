package com.fl.ligueprofantasy.controller;

import android.os.AsyncTask;

import com.fl.ligueprofantasy.model.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class PlayerLab {

    private static PlayerLab sPlayerLab;
    private PlayerLab mPlayerLab;
    private List<Player> mPlayers;
    private List<Player> mPlayersfromApi;


    public static PlayerLab get() {
        if (sPlayerLab == null) {
            sPlayerLab   = new PlayerLab();
        }
        return sPlayerLab;
    }


    private PlayerLab() {

        getPlayersFromDB();
        playerPointsCount(980);
        playerPointsCount(988);
        playerPointsCount(991);
        playerPointsCount(992);
        playerPointsCount(983);
        playerPointsCount(1191);
        playerPointsCount(6252);
        playerPointsCount(990);
        playerPointsCount(993);
        playerPointsCount(6253);
        playerPointsCount(984);
        playerPointsCount(986);
        playerPointsCount(1190);
        playerPointsCount(981);


    }


    public List<Player> getPlayers() {
        return mPlayers;
    }

    public List<Player> getPlayersCopy() {
        return new ArrayList<>(mPlayers);
    }

    public void addPlayer(Player player) {
        mPlayers.add(player);
    }

    public Player getPlayer(int id) {
        for (Player player : mPlayers) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }





    private void getPlayersFromDB() {

        final AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mPlayers = new ArrayList<>();
            }
            @Override
            protected Void doInBackground(Integer... team_ids) {

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://192.168.43.63:4000/players")
                        .get()
                       /* .addHeader("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "14b08a0d95msh9e1340ee09b083ep134dabjsn47eb8ceb1277")

                        */
                        .build();


// c bon
               // Player p1 = new Player(1, "Anice", "Badri", "FWD", 1, 7.0, 40, 1, 10, 0, 8, 8, 1, 1, 0, 0, 4 ,true);
                try {


                    Response response = client.newCall(request).execute();
                    JSONArray array = new JSONArray(response.body().string());
                  //  JSONObject api = jsonObject.getJSONObject("api");
                  //  int results =  api.getInt("results");
                   // JSONArray players = api.getJSONArray("players");

                    for(int i=0; i<array.length(); i++){


                        JSONObject object = array.getJSONObject(i);
                        System.out.println( array.getJSONObject(i)+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

                        String pos ;
                        int id = object.getInt("id");
                        String firstName = object.getString("first_name");
                        String lastName = object.getString("last_name");
                        String position = object.getString("position") ;


                            if (position.equals("Goalkeeper")){
                                pos = "GK";
                            }else if(position.equals("Defender")){
                                pos = "DEF" ;
                            }else if (position.equals("Midfielder")){
                                pos = "MID" ;
                            }else {
                                pos = "FWD";
                            }

                       /* JSONObject games = players.getJSONObject(i).getJSONObject("games");
                        JSONObject substitutes = players.getJSONObject(i).getJSONObject("substitutes");
                        JSONObject goals = players.getJSONObject(i).getJSONObject("goals");
                        JSONObject cards = players.getJSONObject(i).getJSONObject("cards");

                        */
                        int owngls;
                        int appearences = object.getInt("appearences");
                        int goal = object.getInt("goals");
                       // int sub = object.getInt("out");
                        int asist = object.getInt("assists");
                        if(object.isNull("own_goals"))
                        {
                             owngls =0;
                        }
                        else {
                             owngls = object.getInt("own_goals");
                        }

                        int yellow = object.getInt("yellow_cards");
                        int red = object.getInt("red_cards");

                        int team = object.getInt("image");




                        Player player = new Player(id,firstName,lastName,pos,team,10,40,17,appearences,0,goal,asist,0,1,owngls,red,yellow,true);



                        mPlayers.add(player);


                    }
                    System.out.println(mPlayers.get(0).getId()+"ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
//                    String jsonArray = response.body().toString();
//
//                    if (!jsonArray.equals("null")) {
//                        JSONArray array = new JSONArray(jsonArray);
//
//                        for (int i = 0; i < array.length(); i++) {
//                            JSONObject object = array.getJSONObject(i);
//
//                            String pos;
//                            int mAppearences = 0;
//                            int player_id = object.getInt("player_id");
//                            String firtstname = object.getString("firstname");
//                            String lastname = object.getString("lastname") ;
//                            String position = object.getString("position") ;
//                            if (position.equals("Goalkeeper")){
//                                pos = "GK";
//                            }else if(position.equals("Defender")){
//                                pos = "DEF" ;
//                            }else if (position.equals("Midfielder")){
//                                pos = "MID" ;
//                            }else {
//                                pos = "FWD";
//                            }
//
//                            try {
//                                JSONObject rec = array.getJSONObject(i);
//
//                                Iterator<String> keys = rec.keys();
//                                while(keys.hasNext()) {
//                                    String games = keys.next();
//                                    JSONObject nested = rec.getJSONObject(games);
//
//
//                                    Iterator<String> nestedKeys = nested.keys();
//                                    while(nestedKeys.hasNext())  {
//                                        String appearences = nestedKeys.next();
//                                        System.out.println("key"+"..."+appearences+"..."+"value"+"..."+nested.getString(appearences));
//                                         mAppearences = nested.getInt(appearences) ;
//                                    }
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//
//                            Player player = new Player(player_id, firtstname,
//                                    lastname, pos ,
//                                    1, 7.0,
//                                    43, 10,
//                                    mAppearences, 9,
//                                    object.getInt("goals"), 9,
//                                    0, 0,
//                                    9, 9,
//                                    9, true);
//                            mPlayers.add(player);
//                        }
//                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

            catch (
            JSONException e) {
                e.printStackTrace();

            }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

            }
        };

        asyncTask.execute();
    }

   /* private void playerPointsCount() {

        final AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected void onPreExecute() {

            }
            @Override
            protected Void doInBackground(Integer... team_ids) {
                int player_id =0 ;
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                OkHttpClient client = new OkHttpClient();


                for (Player p :  mPlayers){
                    System.out.println(p.getId()+"FiiiiiiiirstNaaaaaaaaaaaaaaaaaaaam");

                    Request request = new Request.Builder()
                            .url("https://api-football-v1.p.rapidapi.com/v2/players/player/"+p.getId()+"/2019-2020")
                            .get()
                            .addHeader("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", "14b08a0d95msh9e1340ee09b083ep134dabjsn47eb8ceb1277")


                            .build();

                    try {
                        Response response = client.newCall(request).execute();

                        JSONObject jsonObject = new JSONObject(response.body().string()) ;
                          JSONObject api = jsonObject.getJSONObject("api");
                          int results =  api.getInt("results");
                         JSONArray players = api.getJSONArray("players");



                        for(int i=0; i<results; i++){
                            if(players.getJSONObject(i).getString("league").equals("Ligue 1")) {
                                String pos;
                                String position = players.getJSONObject(i).getString("position");
                                JSONObject games = players.getJSONObject(i).getJSONObject("games");
                                JSONObject substitutes = players.getJSONObject(i).getJSONObject("substitutes");
                                JSONObject goals = players.getJSONObject(i).getJSONObject("goals");
                                JSONObject cards = players.getJSONObject(i).getJSONObject("cards");
                                int appearences = games.getInt("appearences");
                                int goal = goals.getInt("total");
                                int sub = substitutes.getInt("out");
                                int asist = goals.getInt("assists");
                                int owngls = goals.getInt("conceded");
                                int yellow = cards.getInt("yellow");
                                int red = cards.getInt("red");

                                int goalPoint = (goal-p.getGoals())*3 ;
                                System.out.println(goalPoint+"GOALPOINNNNNNNNNNNTS");
                                p.setGoals(goalPoint);

                            }




                        }



                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    catch (
                            JSONException e) {
                        e.printStackTrace();

                    }



                }





                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        };

        asyncTask.execute();
    }

    */

    private void playerPointsCount(final int team_id) {

        final AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mPlayersfromApi = new ArrayList<>();
            }
            @Override
            protected Void doInBackground(Integer... team_ids) {



                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://api-football-v1.p.rapidapi.com/v2/players/team/"+team_id+"/2019-2020")
                        .get()
                        .addHeader("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "14b08a0d95msh9e1340ee09b083ep134dabjsn47eb8ceb1277")
                        .build();


                try {


                    Response response = client.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONObject api = jsonObject.getJSONObject("api");
                    int results =  api.getInt("results");
                    JSONArray players = api.getJSONArray("players");

                    for(int i=0; i<results; i++){

                        String pos ;
                        int id = players.getJSONObject(i).getInt("player_id");
                        String firstName = players.getJSONObject(i).getString("firstname");
                        String lastName = players.getJSONObject(i).getString("lastname");
                        String position = players.getJSONObject(i).getString("position") ;



                        JSONObject games = players.getJSONObject(i).getJSONObject("games");
                        JSONObject substitutes = players.getJSONObject(i).getJSONObject("substitutes");
                        JSONObject goals = players.getJSONObject(i).getJSONObject("goals");
                        JSONObject cards = players.getJSONObject(i).getJSONObject("cards");
                        int appearences = games.getInt("appearences");
                        int goal = goals.getInt("total");
                        int sub = substitutes.getInt("out");
                        int asist = goals.getInt("assists");
                        int owngls = goals.getInt("conceded");
                        int yellow = cards.getInt("yellow");
                        int red = cards.getInt("red");

                        for (Player p : mPlayers){

                            if (p.getTeam() == team_id && p.getLastName().equals(lastName)){
                                int goalPoint = (goal-p.getGoals())*3 ;

                                p.setPointsWeek(goalPoint);
                                int s = goal*3;
                                p.setPoints(s);

                            }

                        }

                    }


                }catch (IOException e){
                    e.printStackTrace();
                }

                catch (
                        JSONException e) {
                    e.printStackTrace();

                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        };

        asyncTask.execute();
    }

}
