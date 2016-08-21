package com.sitthiphong.smartgardencare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.datamodel.RawDataBean;
import com.sitthiphong.smartgardencare.provider.SimpleDateProvider;

import java.util.Date;
import java.util.List;

/**
 * Created by Sitthiphong on 7/5/2016 AD.
 */
public class MoreAdapter extends RecyclerView.Adapter {
    private static String TAG = MoreAdapter.class.getSimpleName();
    private List<RawDataBean> rawList;
    private Context context;
    private int from;

    public MoreAdapter(Context context, List<RawDataBean> rawList, int from){
        this.context = context;
        this.rawList = rawList;
        this.from = from;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_more, parent, false);
        return  new MoreDataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MoreDataViewHolder){
            RawDataBean bean = rawList.get(position);


            if(from == 1){
                if(bean.getMoistureAverage()<0){
                    ((MoreDataViewHolder) holder).value.setText(R.string.errorSensorMoisture);
                }else{
                    ((MoreDataViewHolder) holder).value.setText(String.valueOf(bean.getMoistureAverage() +" %"));
                }
            }
            else if(from == 2){
                if(bean.getTempAverage()<0){
                    ((MoreDataViewHolder) holder).value.setText(R.string.errorSensorDHT22);
                }
                else {
                    ((MoreDataViewHolder) holder).value.setText(String.valueOf(bean.getTempAverage() +" °C"));
                }

            }
            else if(from == 3){
                if(bean.getLightIn()<0 && bean.getLightOut()<0){
                    ((MoreDataViewHolder) holder).value.setText(R.string.errorSensorBH1750);
                }
                else{
                    ((MoreDataViewHolder) holder).value.setText(String.valueOf(
                            context.getResources().getString(R.string.lightIn) +": "
                            +bean.getLightIn() +" Lux, "+
                                    context.getResources().getString(R.string.lightOut)+": "
                                    +bean.getLightOut()+" Lux"));
                }

            }

            ((MoreDataViewHolder) holder).dateTime.setText(SimpleDateProvider.getInstance().format(new Date(bean.getTime()*1000)));



        }

    }

    @Override
    public int getItemCount() {
        return rawList.size();
    }

    public List<RawDataBean> getRawList() {
        return rawList;
    }

    public void setRawList(List<RawDataBean> rawList) {
        this.rawList = rawList;
    }

    public static class MoreDataViewHolder extends RecyclerView.ViewHolder{
        public TextView value;
        public TextView dateTime;

        public MoreDataViewHolder(View itemView) {
            super(itemView);
            value = (TextView)itemView.findViewById(R.id.action_more);
            dateTime = (TextView)itemView.findViewById(R.id.time_more);
        }
    }
}
