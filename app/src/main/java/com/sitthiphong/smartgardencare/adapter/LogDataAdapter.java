package com.sitthiphong.smartgardencare.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.datamodel.LogDataBean;
import com.sitthiphong.smartgardencare.provider.SimpleDateProvider;

import java.util.Date;
import java.util.List;

/**
 * Created by zaade on 24/6/2559.
 */
public class LogDataAdapter extends RecyclerView.Adapter<LogDataAdapter.LogDataViewHolder> {
    private static String TAG = LogDataAdapter.class.getSimpleName();
    private List<LogDataBean> logList;
    private Context context;

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
            //Log.e(TAG,"Action working: "+bean.getAction());

            holder.dateTime.setText(SimpleDateProvider.getInstance()
                    .format(new Date(bean.getTime() * 1000)));
            holder.typeAction.setText((bean.getType() == 1) ? context.getString(R.string.typeActionAuto) :
                    context.getString(R.string.typeActionManual));

            if (bean.getAction() <= 4) {
                switch (bean.getAction()) {
                    case 1:
                        holder.action.setText(context.getString(R.string.water));
                        holder.beforeAndAfter.setText(
                                context.getString(R.string.before) + " " +
                                        String.valueOf(String.format("%.2f", bean.getValBefore()) + " %, " +
                                                context.getString(R.string.after) + " " +
                                                String.valueOf(String.format("%.2f", bean.getValAfter()) + " %")));
                        break;
                    case 2:
                        holder.action.setText(context.getString(R.string.shower));
                        holder.beforeAndAfter.setText(
                                context.getString(R.string.before) + " " +
                                        String.valueOf(String.format("%.2f", bean.getValBefore()) + " °C, " +
                                                context.getString(R.string.after) + " " +
                                                String.valueOf(String.format("%.2f", bean.getValAfter()) + " °C")));
                        break;
                    case 3:
                        holder.action.setText(context.getString(R.string.acOpenSlat));
                        holder.beforeAndAfter.setVisibility(View.GONE);
                        break;
                    case 4:
                        holder.action.setText(context.getString(R.string.acCloseSlat));
                        holder.beforeAndAfter.setText(
                                context.getString(R.string.before) + " " +
                                        String.valueOf(String.format("%.2f", bean.getValBefore()) + " Lux, " +
                                                context.getString(R.string.after) + " " +
                                                String.valueOf(String.format("%.2f", bean.getValAfter()) + " Lux")));
                        break;
                    default:

                }

            } else {
                holder.action.setTextColor(ContextCompat.getColor(context, R.color.red));
                holder.dateTime.setTextColor(ContextCompat.getColor(context, R.color.red));
                holder.typeAction.setTextColor(ContextCompat.getColor(context, R.color.red));
                holder.beforeAndAfter.setTextColor(ContextCompat.getColor(context, R.color.red));
                switch (bean.getAction()) {
                    case 5:
                        holder.action.setText(context.getString(R.string.water));
                        holder.beforeAndAfter.setText(context.getString(R.string.waterFalse));
                        break;
                    case 6:
                        holder.action.setText(context.getString(R.string.water));
                        holder.beforeAndAfter.setText(context.getString(R.string.noWateringArea));
                        break;
                    case 7:
                        holder.action.setText(context.getString(R.string.shower));
                        holder.beforeAndAfter.setText(context.getString(R.string.waterFalse));
                        break;
                    case 8:
                        holder.action.setText(context.getString(R.string.shower));
                        holder.beforeAndAfter.setText(context.getString(R.string.tempNotDecrease));
                        break;
                    case 9:
                        holder.action.setText(context.getString(R.string.acCloseSlat));
                        holder.beforeAndAfter.setText(context.getString(R.string.lightInNotDecrease));
                        break;
                    default:

                }
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

    public static class LogDataViewHolder extends RecyclerView.ViewHolder {
        private static String TAG = LogDataViewHolder.class.getSimpleName();
        public TextView action;
        public TextView dateTime;
        public TextView typeAction;
        public TextView beforeAndAfter;


        public LogDataViewHolder(View itemView) {
            super(itemView);
            action = (TextView) itemView.findViewById(R.id.tv_cv_action_logData);
            dateTime = (TextView) itemView.findViewById(R.id.tv_cv_timeStamp_logData);
            typeAction = (TextView) itemView.findViewById(R.id.tv_cv_type_logData);
            beforeAndAfter = (TextView) itemView.findViewById(R.id.tv_before_after);

        }

    }
}
