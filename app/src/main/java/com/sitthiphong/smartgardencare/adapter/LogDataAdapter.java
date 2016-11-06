package com.sitthiphong.smartgardencare.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.datamodel.ConfigData;
import com.sitthiphong.smartgardencare.datamodel.LogDataBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zaade on 24/6/2559.
 */
public class LogDataAdapter extends RecyclerView.Adapter<LogDataAdapter.LogDataViewHolder> {
    private static String TAG = LogDataAdapter.class.getSimpleName();
    private List<LogDataBean> logList;
    public Context context;

    public LogDataAdapter(Context context, List<LogDataBean> logList) {
        this.logList = logList;
        this.context = context;
    }

    @Override
    public LogDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_log_data, parent, false);
        return new LogDataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LogDataViewHolder holder, int position) {
        if (holder instanceof LogDataViewHolder) {
            LogDataBean bean = logList.get(position);


            holder.time.setText(getDateTime(bean.getTime() * 1000));
            if (bean.getType() == ConfigData.AUTO_TYPE) {
                holder.type.setText(context.getString(R.string.autoMode));
            } else if (bean.getType() == ConfigData.MANUAL_TYPE) {
                holder.type.setText(context.getString(R.string.manualMode));
            } else if(bean.getType() == 3){
                holder.type.setText(context.getString(R.string.alarmClock));
            }

            switch (bean.getWorking()) {
                case 1:
                    holder.working.setText(context.getString(R.string.water));
                    holder.beforeAndAfter.setText(getBeforeAndAfter(context,bean,"%"));
                    break;
                case 2:
                    holder.working.setText(context.getString(R.string.foggy));
                    holder.beforeAndAfter.setText(getBeforeAndAfter(context,bean,"°C"));
                    break;
                case 3:
                    holder.working.setText(context.getString(R.string.openSlat));
                    holder.beforeAndAfter.setText(getBeforeAndAfter(context,bean,"Lux"));
                    break;
                case 4:
                    holder.working.setText(context.getString(R.string.closeSlat));
                    holder.beforeAndAfter.setText(getBeforeAndAfter(context,bean,"Lux"));
                    break;
                default:
                    break;
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

    private String getBeforeAndAfter(Context context, LogDataBean bean, String unit) {
        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.before));
        builder.append(" " + String.valueOf(bean.getVal_berfore()));
        builder.append(" " + unit + ", ");
        builder.append(context.getString(R.string.after) + " ");
        builder.append(String.valueOf(bean.getVal_after()));
        builder.append(" " + unit);
        return builder.toString();
    }

    private String getDateTime(long time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            Date date = (new Date(time));
            return dateFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public void setLogList(List<LogDataBean> logList) {
        this.logList = logList;
    }


    public class LogDataViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = "LogDataViewHolder";
        public TextView working;
        public TextView time;
        public TextView type;
        public TextView beforeAndAfter;


        public LogDataViewHolder(View itemView) {
            super(itemView);
            working = (TextView) itemView.findViewById(R.id.tv_working);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            type = (TextView) itemView.findViewById(R.id.tv_type);
            beforeAndAfter = (TextView) itemView.findViewById(R.id.tv_val_before_after);


        }


    }

}
