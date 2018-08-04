package com.example.gouthamishivaprakash.howler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gouthamishivaprakash.howler.model.Alarms;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<Alarms> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Realm realm;
    private RealmResults<Alarms> results;
    private onClickTrashIconListener mOnClickTrashIconListener;

    // data is passed into the constructor
    AlarmAdapter(Context context, RealmResults<Alarms> data, Realm realm) {
        this.mInflater = LayoutInflater.from(context);
        this.results = data.sort("id",Sort.ASCENDING);;
        this.realm = realm;
        mData = realm.copyFromRealm(results);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.alarm_time, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the CardView in each row
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String alarm = mData.get(position).getAlarmTime();
        holder.myTextView.setText(alarm);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // allows click events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    //custom listener to delete alarm on delete icon clicked
    public interface onClickTrashIconListener {
        void cancelPendingIntent(String time);
    }

    //setter which allows the listener callbacks to be defined in the parent object
    public void setOnClickTrashIconListener(onClickTrashIconListener mOnClickTrashIconListener) {
        this.mOnClickTrashIconListener = mOnClickTrashIconListener;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.alarmTime);
            deleteButton = itemView.findViewById(R.id.delbut);
            deleteButton.setOnClickListener(view -> {
                int iD = getAdapterPosition();
                String time = myTextView.getText().toString();
                deleteAlarm(iD, time);
            });
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void updateAdapter(Alarms alarm) {
        mData.add(0,alarm);
        notifyDataSetChanged();
    }

    public void deleteAlarm(int position, String setTime) {
        mOnClickTrashIconListener.cancelPendingIntent(setTime);
        mData.remove(position);
        notifyDataSetChanged();
    }

}
