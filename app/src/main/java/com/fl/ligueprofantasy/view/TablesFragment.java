package com.fl.ligueprofantasy.view;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fl.ligueprofantasy.R;
import com.fl.ligueprofantasy.adapters.FixturesAdapter;
import com.fl.ligueprofantasy.model.Fixture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public  class TablesFragment extends Fragment
         {

    public static final String TAG = "FragmentFixtures";

    private RecyclerView mRecyclerView;
    private FrameLayout mProgressContent;
    private LinearLayout mErrorContent;
    private LinearLayout mEmptyContent;
    private TextView mMatchdayField;
    private FixturesAdapter mAdapter;
   /* private Controller mController;
    private EventListener mEventListener;
    private Snackbar mSnackbar;
    private League mLeague;
    private int mMatchday; */
    private boolean mIsShow;
    private List<Fixture>  fixtures ;
             private List<Fixture>  mFixtures ;
    private  RecyclerView mFixturesRecycleView ;
    private View view;
    private LinearLayoutManager linearLayout;
    Context context ;

    public static TablesFragment getInstance(@Nullable Bundle data) {
        TablesFragment fragment = new TablesFragment();
        fragment.setArguments(data == null ? new Bundle() : data);
        return fragment;
    }

             public TablesFragment() {
                 // Required empty public constructor
             }

             @Override
             public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                 view  = inflater.inflate(R.layout.fragment_tables, container, false);


        fixtures = new ArrayList<>();
        mFixturesRecycleView =  view.findViewById(R.id.recycler_view_fix);
        linearLayout = new LinearLayoutManager(context);
        mFixturesRecycleView.setLayoutManager(linearLayout);

        getFixturesFromAPI();


       // updateUI();

        return view ;
    }




            /* private void getFixturesFromAPI(){

                 fixtures = new ArrayList<>();

                 Fixture t1 = new Fixture(1, new Date(),"13","finished","rades","mkach5a","tp mazembe","1-0",1,0);
                 Fixture t2 = new Fixture(1, new Date(),"13","finished","rades","mkach5a","tp mazembe","1-0",1,0);
                 Fixture t3 = new Fixture(1, new Date(),"13","finished","rades","mkach5a","tp mazembe","1-0",1,0);
                 fixtures.add(t1);
                 fixtures.add(t2);
                 fixtures.add(t3);
             }*/


             private void getFixturesFromAPI() {

                 final AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {
                     @Override
                     protected void onPreExecute() {
                         mFixtures = new ArrayList<>();
                     }
                     @Override
                     protected Void doInBackground(Integer... team_ids) {


                         HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                         logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                         OkHttpClient client = new OkHttpClient();
                         Request request = new Request.Builder()
                                 .url("https://api-football-v1.p.rapidapi.com/v2/fixtures/league/897/Regular_Season_-_13")
                                 .get()
                                 .addHeader("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                                 .addHeader("x-rapidapi-key", "14b08a0d95msh9e1340ee09b083ep134dabjsn47eb8ceb1277")
                                 .build();


// c bon
                         // Player p1 = new Player(1, "Anice", "Badri", "FWD", 1, 7.0, 40, 1, 10, 0, 8, 8, 1, 1, 0, 0, 4 ,true);
                         try {


                             Response response = client.newCall(request).execute();
                             JSONObject jsonObject = new JSONObject(response.body().string());
                             JSONObject api = jsonObject.getJSONObject("api");
                             int results =  api.getInt("results");
                             JSONArray players = api.getJSONArray("fixtures");

                             for(int i=0; i<results; i++){

                                 int  goalsAwayTeamInt = 0;
                                 int goalsHomeTeamInt = 0 ;

                                 String pos ;
                                 int id = players.getJSONObject(i).getInt("fixture_id");
                                 String date = players.getJSONObject(i).getString("event_date");

                                 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
                                 String dateInString = date;

                                 Date mDate = formatter.parse(dateInString);

                                 String round = players.getJSONObject(i).getString("round");
                                 String status = players.getJSONObject(i).getString("status") ;


                                 String venue = players.getJSONObject(i).getString("venue");

                                 JSONObject home = players.getJSONObject(i).getJSONObject("homeTeam");
                                 JSONObject away = players.getJSONObject(i).getJSONObject("awayTeam");
                                 String homeTeam = home.getString("team_name");
                                 String awayTeam = away.getString("team_name");
                                 String goalsHomeTeam = players.getJSONObject(i).getString("goalsHomeTeam");

                                 if(goalsHomeTeam.equals("null") ){
                                     goalsAwayTeamInt = -1;
                                     goalsHomeTeamInt = -1 ;
                                 }else {
                                      goalsHomeTeamInt = players.getJSONObject(i).getInt("goalsHomeTeam");
                                      goalsAwayTeamInt = players.getJSONObject(i).getInt("goalsAwayTeam");
                                 }
                                 String homeImg = home.getString("logo");
                                 String awayImg = away.getString("logo");


                                 Fixture fixture = new Fixture(id,mDate,round,status,venue,homeTeam,awayTeam,"0-0",goalsHomeTeamInt,goalsAwayTeamInt,homeImg,awayImg);
                                 mFixtures.add(fixture);

                             }


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

                         } catch (ParseException e) {
                             e.printStackTrace();
                         }


                         return null;
                     }

                     @Override
                     protected void onPostExecute(Void aVoid) {
                         mAdapter = new FixturesAdapter(getResources(),mFixtures);
                         mFixturesRecycleView.setAdapter(mAdapter);
                     }
                 };

                 asyncTask.execute();
             }

           /*  private void updateUI() {

                 mAdapter = new FixturesAdapter(getResources(),mFixtures);
                 mFixturesRecycleView.setAdapter(mAdapter);
             }*/


  /*  @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tables, container, false);
        setupUI(view);
        if (mEventListener == null) mEventListener = (EventListener) getActivity();
        if (mController == null) mController = Controller.getInstance();
        showContent();
        mIsShow = true;
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRecyclerView != null) mRecyclerView = null;
        if (mEmptyContent != null) mEmptyContent = null;
        if (mErrorContent != null) mErrorContent = null;
        if (mProgressContent != null) mProgressContent = null;
        if (mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
        mIsShow = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("matchday", mMatchday);
        if (mAdapter != null) {
            ArrayList<Fixture> list = mAdapter.getList();
            if (!list.isEmpty()) outState.putParcelableArrayList("list", list);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEventListener != null) mEventListener = null;
        if (mMatchdayField != null) mMatchdayField = null;
        if (mController != null) mController = null;
        if (mAdapter != null) mAdapter = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_fixtures, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share:
                if (!mAdapter.getList().isEmpty()) {
                    mEventListener.onEvent(
                            new EventData(Const.EVENT_CODE_SHARE_FIXTURES_LIST)
                                    .setLeague(mLeague)
                                    .setFixturesList(mAdapter.getList())
                    );
                }
                break;

            case R.id.action_current:
                mMatchday = mLeague.getCurrentMatchday();
                loadData(mMatchday);
                break;

            case R.id.action_scores_table:
                mEventListener.onEvent(
                        new EventData(Const.EVENT_CODE_SHOW_SCORES_TABLE)
                                .setLeague(mLeague)
                );
                break;

            case R.id.action_settings:
                mEventListener.onEvent(new EventData(Const.EVENT_CODE_SHOW_SETTINGS));
                break;

            case R.id.action_about:
                mEventListener.onEvent(new EventData(Const.EVENT_CODE_SHOW_ABOUT));
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int id = v.getId();
        switch (id) {
            case R.id.matchday:
                menu.setHeaderTitle(getString(R.string.context_head_select_matchday));
                for (int i = 1; i <= mLeague.getNumberOfMatchdays(); i++) {
                    menu.add(Const.GROUP_MATCHDAYS, i, 0, String.valueOf(i));
                }
                break;

            default:
                super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int group = item.getGroupId();
        switch (group) {
            case Const.GROUP_MATCHDAYS:
                mMatchday = item.getItemId();
                loadData(mMatchday);
                break;

            case Const.GROUP_FIXTURES:
                mController.changeNotification(getActivity(), item.getItemId(), mAdapter);
                break;

            default:
                L.e(FragmentFixtures.class, "default group->" + group);
                return super.onContextItemSelected(item);
        }
        return true;
    }

    @Override
    public void onError(int code) {
        if (mIsShow && (mAdapter == null || mAdapter.getList().isEmpty())) {
            if (code == Const.ERROR_CODE_RESULT_EMPTY) {
                UI.hide(mRecyclerView, mErrorContent, mProgressContent);
                UI.show(mEmptyContent);
            } else {
                UI.hide(mRecyclerView, mEmptyContent, mProgressContent);
                UI.show(mErrorContent);
                mSnackbar = Snackbar.make(
                        getActivity().findViewById(R.id.main_container),
                        R.string.snackbar_result_null_text,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(
                                R.string.snackbar_result_null_action,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadData(mMatchday);
                                    }
                                }
                        );
                mSnackbar.show();
            }
        }
    }

    @Override
    public void onSuccess(ArrayList<Fixture> data) {
        if (mIsShow) {
            UI.hide(mErrorContent, mEmptyContent, mProgressContent);
            UI.show(mRecyclerView);
            if (mSnackbar != null) mSnackbar.dismiss();
            if (mAdapter == null) {
                mAdapter = new FixturesAdapter(getResources(), data);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.changeData(data);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        mEventListener.onEvent(new EventData(Const.EVENT_CODE_SELECT_FIXTURE_INFO)
                .setFixture(mAdapter.getItem(position)));
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Fixture fixture = mAdapter.getItem(position);
        if (fixture != null) {
            if (fixture.getDate() > System.currentTimeMillis() + 1000 * 60 * 60
                    || fixture.isNotified()) { // one day
                view.showContextMenu();
            }
        }
    }

    private void setupUI(View view) {
        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(mLeague.getCaption());
            toolbar.setSubtitle(null);
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false));
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemTouchListener(
                getActivity(),
                mRecyclerView,
                this));
        mRecyclerView.setHasFixedSize(true);
        mMatchdayField = (TextView) view.findViewById(R.id.matchday);
        mMatchdayField.setText(String.format(
                getString(R.string.fixtures_matchday),
                mMatchday));
        mMatchdayField.setOnCreateContextMenuListener(this);
        mMatchdayField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().openContextMenu(v);
            }
        });
        mProgressContent = (FrameLayout) view.findViewById(R.id.content_progress);
        mEmptyContent = (LinearLayout) view.findViewById(R.id.content_empty);
        mErrorContent = (LinearLayout) view.findViewById(R.id.content_error);
        UI.hide(mEmptyContent, mErrorContent, mProgressContent, mRecyclerView);
    }

    private void showContent() {
        if (mAdapter == null) loadData(mMatchday);
        else {
            UI.hide(mEmptyContent, mErrorContent, mProgressContent);
            UI.show(mRecyclerView);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void loadData(int matchday) {
        UI.hide(mRecyclerView, mEmptyContent, mErrorContent);
        UI.show(mProgressContent);
        if (mAdapter != null) mAdapter = null;
        setMatchday(matchday);
        mController.getListOfFixtures(getActivity(), mLeague.getID(), matchday, this);
    }

    private void setMatchday(int matchday) {
        mMatchdayField.setText(String.format(getString(R.string.fixtures_matchday), matchday));
    }

*/

}
