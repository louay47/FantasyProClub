package com.fl.ligueprofantasy.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fl.ligueprofantasy.R;
import com.fl.ligueprofantasy.model.Fixture;
import com.squareup.picasso.Picasso;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public  class FixturesAdapter extends RecyclerView.Adapter<FixturesAdapter.FixturesViewHolder> {
    private Resources mResources;
    private Date mDate;
    private Date mDateTmp;
    private Calendar mCalendar;
    private Calendar mCalendarTmp;
    List<Fixture> mlist ;
    Fixture fixture;
    Context context ;

    public FixturesAdapter( Resources ressources ,List<Fixture> list) {

        mResources = ressources ;
        mCalendar = Calendar.getInstance();
        mCalendarTmp = Calendar.getInstance();
        mDate = new Date();
        mDateTmp = new Date();
        mlist = list ;
    }

    @Override
    public FixturesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.match_row, parent, false);
        return new FixturesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FixturesViewHolder holder, int position) {
        Fixture item = mlist.get(position);
        holder.mHomeTeamName.setText(item.getHomeTeam());
        holder.mAwayTeamName.setText(item.getAwayTeam());
        ImageView homeImage =holder.homeImg;
        ImageView awayImage =holder.awayImg;
        Picasso.get().load(item.getAwayImg()).into(awayImage);
        Picasso.get().load(item.getHomeImg()).into(homeImage);
        int goalsHomeTeam = item.getGoalsHomeTeam();
        int goalsAwayTeam = item.getGoalsAwayTeam();
        if (goalsHomeTeam >= 0 && goalsAwayTeam >= 0) {
            UI.hide(holder.mDate, holder.mBell);
            UI.show(holder.mResult);
            holder.mResult.setText(String.format(mResources.getString(
                    R.string.fixtures_list_item_result),
                    goalsHomeTeam,
                    goalsAwayTeam)
            );
            holder.mResult.setBackgroundResource(R.drawable.fixtures_list_item_result_background);
            if (goalsHomeTeam > goalsAwayTeam) {
                holder.mHomeTeamName.setTextColor(
                        mResources.getColor(R.color.fixtures_list_item_win_name)
                );
                holder.mAwayTeamName.setTextColor(
                        mResources.getColor(R.color.fixtures_list_item_lose_name)
                );
            }
            else if (goalsHomeTeam < goalsAwayTeam) {
                holder.mAwayTeamName.setTextColor(
                        mResources.getColor(R.color.fixtures_list_item_win_name)
                );
                holder.mHomeTeamName.setTextColor(
                        mResources.getColor(R.color.fixtures_list_item_lose_name)
                );
            }
            else {
                holder.mAwayTeamName.setTextColor(
                        mResources.getColor(R.color.fixtures_list_item_draw_name)
                );
                holder.mHomeTeamName.setTextColor(
                        mResources.getColor(R.color.fixtures_list_item_draw_name)
                );
            }
        }
        else {
            UI.hide(holder.mResult, holder.mBell);
            UI.show(holder.mDate);
             UI.show(holder.mBell);
            DateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            holder.mDate.setText(format.format(new Date(item.getEvent_date().getTime())));
            holder.mAwayTeamName.setTextColor(
                    mResources.getColor(R.color.fixtures_list_item_name)
            );
            holder.mHomeTeamName.setTextColor(
                    mResources.getColor(R.color.fixtures_list_item_name)
            );
        }

        mDate.setTime(item.getEvent_date().getTime());
        mCalendar.setTime(mDate);
        if (position > 0) {
            mDateTmp.setTime(mlist.get(position - 1).getEvent_date().getTime());
            mCalendarTmp.setTime(mDateTmp);
        }
        if (position == 0
                || (mCalendar.get(Calendar.DAY_OF_YEAR)
                != mCalendarTmp.get(Calendar.DAY_OF_YEAR))) {
            mDateTmp.setTime(System.currentTimeMillis());
            mCalendarTmp.setTime(mDateTmp);
            if (mCalendar.get(Calendar.YEAR) == mCalendarTmp.get(Calendar.YEAR)
                    && mCalendar.get(Calendar.DAY_OF_YEAR)
                    == mCalendarTmp.get(Calendar.DAY_OF_YEAR)) {
                holder.mHeader.setText(
                        mResources.getString(R.string.fixtures_list_item_header_today));
            }
            else if (mCalendar.get(Calendar.YEAR) == mCalendarTmp.get(Calendar.YEAR)
                    && mCalendar.get(Calendar.DAY_OF_YEAR)
                    == mCalendarTmp.get(Calendar.DAY_OF_YEAR) + 1) {
                holder.mHeader.setText(
                        mResources.getString(R.string.fixtures_list_item_header_tomorrow));
            }
            else {
                holder.mHeader.setText(
                        new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                .format(new Date(item.getEvent_date().getTime())));
            }
            UI.show(holder.mHeader);
        }
        else UI.hide(holder.mHeader);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public  class FixturesViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {

        private TextView mHomeTeamName,
                mAwayTeamName,
                mDate,
                mResult,
                mHeader;
        private ImageView mBell,homeImg,awayImg;

        public FixturesViewHolder(View itemView) {
            super(itemView);
            mHomeTeamName = (TextView) itemView.findViewById(R.id.home_team_name);
            mAwayTeamName = (TextView) itemView.findViewById(R.id.away_team_name);
            mDate = (TextView) itemView.findViewById(R.id.date);
            mResult = (TextView) itemView.findViewById(R.id.result);
            mHeader = (TextView) itemView.findViewById(R.id.header);
            mBell = (ImageView) itemView.findViewById(R.id.bell);
            homeImg = itemView.findViewById(R.id.homeImg);
            awayImg = itemView.findViewById(R.id.awayImg);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(
                ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(
                    1, getAdapterPosition(), 0,
                    "Notification");
        }
    }
}
