package com.sitthiphong.smartgardencare.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.bean.LogDataBean;
import com.sitthiphong.smartgardencare.provider.SimpleDateProvider;

import java.util.Date;
import java.util.List;

/**
 * Created by zaade on 24/6/2559.
 */
public class LogDataAdapter extends RecyclerView.Adapter {
    private static String TAG = LogDataAdapter.class.getSimpleName();
    private List<LogDataBean> logList;
    private Context context;

    public LogDataAdapter(Context context, List<LogDataBean> logList) {
        this.logList = logList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_log_data, parent, false);
        return  new LogDataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LogDataViewHolder){
            LogDataBean bean = logList.get(position);

            ((LogDataViewHolder)holder).action.setText(bean.getAction());
            ((LogDataViewHolder)holder).dateTime.setText(SimpleDateProvider.getInstance()
                                                            .format(new Date(bean.getTimeStamp()*1000)));
            ((LogDataViewHolder)holder).typeAction.setText(bean.getTypeAction());

            if(bean.getNote() != null){
                ((LogDataViewHolder)holder).note.setText(bean.getNote());
                ((LogDataViewHolder)holder).note.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public List<LogDataBean> getLogList() {
        return logList;
    }

    public void setLogList(List<LogDataBean> logList) {
        this.logList = logList;
    }

    public static class LogDataViewHolder extends RecyclerView.ViewHolder{
        private static String TAG = LogDataViewHolder.class.getSimpleName();
        public TextView action;
        public TextView dateTime;
        public TextView typeAction;
        public TextView note;


        public LogDataViewHolder(View itemView) {
            super(itemView);
            action = (TextView)itemView.findViewById(R.id.tv_cv_action_logData);
            dateTime = (TextView)itemView.findViewById(R.id.tv_cv_timeStamp_logData);
            typeAction = (TextView)itemView.findViewById(R.id.tv_cv_type_logData);
            note = (TextView)itemView.findViewById(R.id.tv_cv_note_logData);
            note.setVisibility(View.GONE);
        }

    }
}
