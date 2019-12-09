package com.sitthiphong.smartgardencare.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.activity.SetClockActivity;
import com.sitthiphong.smartgardencare.datamodel.AlarmClockBean;
import com.sitthiphong.smartgardencare.datamodel.ConfigData;
import com.sitthiphong.smartgardencare.libs.TimePickerFragment;

import java.util.List;

/**
 * Created by Sitthiphong on 11/4/2016 AD.
 */

public class AlarmClockAdapter extends RecyclerView.Adapter<AlarmClockAdapter.AlarmClockViewHolder> {

    private Context context;
    private List<AlarmClockBean> alarmClockList;
    private OnRemoveClockListener onRemoveClockListener;
    private HaveChangeListener haveChangeListener;
    private boolean onBind;

    public AlarmClockAdapter(Context context, List<AlarmClockBean> alarmClockList) {
        this.context = context;
        this.alarmClockList = alarmClockList;
    }

    public void setOnRemoveClock(OnRemoveClockListener onRemoveClockListener) {
        this.onRemoveClockListener = onRemoveClockListener;
    }

    public void setHaveChangeListener(HaveChangeListener haveChangeListener) {
        this.haveChangeListener = haveChangeListener;
    }

    @Override
    public AlarmClockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlarmClockViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_clock, parent, false));
    }

    @Override
    public void onBindViewHolder(AlarmClockViewHolder holder, int position) {

        onBind = true;
        AlarmClockBean alarmClockBean = alarmClockList.get(position);

        holder.time.setText(alarmClockBean.getTimeText());
        if (alarmClockBean.isOpen()) {
            holder.openClock.setChecked(true);
        }

        switch (alarmClockBean.getWorking()) {
            case 0:
                holder.working.setText(context.getString(R.string.selectWorking));
                break;
            case 1:
                holder.working.setText(context.getString(R.string.water));
                break;
            case 2:
                holder.working.setText(context.getString(R.string.foggy));
                break;
            case 3:
                holder.working.setText(context.getString(R.string.openSlat));
                break;
            case 4:
                holder.working.setText(context.getString(R.string.closeSlat));
                break;
            default:
                break;
        }

        if (alarmClockBean.isRepeat()) {
            holder.layoutDays.setVisibility(View.VISIBLE);
            holder.repeat.setChecked(true);
        } else {
            holder.layoutDays.setVisibility(View.GONE);
            holder.repeat.setChecked(false);
        }
        for (int i = 0; i < alarmClockBean.getDaysSet().size(); i++) {
            switch (alarmClockBean.getDaysSet().get(i)) {
                case ConfigData.monDay:
                    holder.addDay(holder.monDay);
                    break;
                case ConfigData.tuesDay:
                    holder.addDay(holder.tuesDay);
                    break;
                case ConfigData.wednesDay:
                    holder.addDay(holder.wednesDay);
                    break;
                case ConfigData.thursDay:
                    holder.addDay(holder.thursDay);
                    break;
                case ConfigData.friDay:
                    holder.addDay(holder.friDay);
                    break;
                case ConfigData.saturDay:
                    holder.addDay(holder.saturDay);
                    break;
                case ConfigData.sunDay:
                    holder.addDay(holder.sunDay);
                    break;
                default:
                    break;
            }
        }

        onBind = false;


    }

    @Override
    public int getItemCount() {
        return alarmClockList.size();
    }

    public List<AlarmClockBean> getAlarmClockList() {
        return alarmClockList;
    }

    public class AlarmClockViewHolder extends RecyclerView.ViewHolder {

        public TextView time;
        public Switch openClock;
        public CheckBox repeat;
        public LinearLayout layoutDays;
        public TextView working, monDay, tuesDay, wednesDay, thursDay, friDay, saturDay, sunDay;
        public ImageButton btnDeleteClock;
        private AlarmClockBean alarmClockBean;

        public AlarmClockViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.tv_clock);
            openClock = itemView.findViewById(R.id.sw_open_clock);
            working = itemView.findViewById(R.id.tv_working);
            repeat = itemView.findViewById(R.id.cb_repeat);
            layoutDays = itemView.findViewById(R.id.layout_days);
            monDay = itemView.findViewById(R.id.monDay);
            tuesDay = itemView.findViewById(R.id.tuesDay);
            wednesDay = itemView.findViewById(R.id.wednesDay);
            thursDay = itemView.findViewById(R.id.thursDay);
            friDay = itemView.findViewById(R.id.friDay);
            saturDay = itemView.findViewById(R.id.saturDay);
            sunDay = itemView.findViewById(R.id.sunDay);
            btnDeleteClock = itemView.findViewById(R.id.btn_delete_time);

            time.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    alarmClockBean = alarmClockList.get(getAdapterPosition());

                    TimePickerFragment timePicker = new TimePickerFragment(alarmClockBean.getHour(), alarmClockBean.getMinute());
                    timePicker.setOnTimeSetListener(new TimePickerFragment.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute, String timeText) {

                            alarmClockBean.setHour(hourOfDay);
                            alarmClockBean.setMinute(minute);
                            alarmClockList.set(getAdapterPosition(), alarmClockBean);
                            time.setText(timeText);
                            notifyDataSetChanged();
                            updateChange();

                        }
                    });
                    timePicker.show(((SetClockActivity) context).getFragmentManager(), "TimePicker");

                }
            });
            working.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alarmClockBean = alarmClockList.get(getAdapterPosition());
                    listDialog(context.getString(R.string.selectWorking),
                            R.array.selectWorkingArray,
                            alarmClockBean.getWorking(),
                            working);
                }
            });

            openClock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    alarmClockBean = alarmClockList.get(getAdapterPosition());
                    alarmClockBean.setOpen(openClock.isChecked());
                    alarmClockList.set(getAdapterPosition(), alarmClockBean);
                    if (!onBind) {
                        notifyDataSetChanged();
                        updateChange();
                    }
                    alarmClockBean = null;
                }
            });

            repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    alarmClockBean = alarmClockList.get(getAdapterPosition());
                    alarmClockBean.setRepeat(b);
                    if (repeat.isChecked()) {
                        layoutDays.setVisibility(View.VISIBLE);
                    } else {
                        layoutDays.setVisibility(View.GONE);
                    }
                    alarmClockList.set(getAdapterPosition(), alarmClockBean);

                    if (!onBind) {
                        notifyDataSetChanged();
                        updateChange();
                    }
                    alarmClockBean = null;
                }
            });

            monDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickDaySet(monDay, ConfigData.monDay);
                }
            });
            tuesDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickDaySet(tuesDay, ConfigData.tuesDay);
                }
            });
            wednesDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickDaySet(wednesDay, ConfigData.wednesDay);
                }
            });
            thursDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickDaySet(thursDay, ConfigData.thursDay);
                }
            });
            friDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickDaySet(friDay, ConfigData.friDay);
                }
            });
            saturDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickDaySet(saturDay, ConfigData.saturDay);
                }
            });
            sunDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickDaySet(sunDay, ConfigData.sunDay);
                }
            });

            btnDeleteClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogDeleteClock();
                }
            });

        }

        private void onClickDaySet(TextView day, String d) {
            Log.e("AlarmClockAdapter", "onClickDaySet");

            alarmClockBean = alarmClockList.get(getAdapterPosition());
            List<String> daysSet = alarmClockBean.getDaysSet();

            boolean isRemove = false;
            for (int i = 0; i < daysSet.size(); i++) {
                if (daysSet.get(i).equals(d)) {
                    //Log.e("AlarmClockAdapter", "remove day: " + d);
                    isRemove = true;
                    daysSet.remove(i);
                    day.setBackground(null);
                    day.setTextColor(ContextCompat.getColor(context, R.color.SecondaryText_Black));
                    i = daysSet.size();
                }
            }
            if (!isRemove) {
                //Log.e("AlarmClockAdapter", "add day: " + d);
                daysSet.add(d);
                day.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_set_clock));
                day.setTextColor(ContextCompat.getColor(context, R.color.PrimaryText_White));

            }
            alarmClockBean.setDaysSet(daysSet);
            alarmClockList.set(getAdapterPosition(), alarmClockBean);
            notifyDataSetChanged();
            updateChange();
            alarmClockBean = null;
        }

        private void addDay(TextView day) {
            day.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_set_clock));
            day.setTextColor(ContextCompat.getColor(context, R.color.PrimaryText_White));
        }


        private void listDialog(String title, final int arrId, int values, final TextView textView) {
            new MaterialDialog.Builder(context)
                    .title(title)
                    .items(arrId)
                    .itemsCallbackSingleChoice((values - 1),
                            new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    //alarmClockBean = alarmClockList.get(getAdapterPosition());
                                    alarmClockBean.setWorking((which + 1));
                                    alarmClockList.set(getAdapterPosition(), alarmClockBean);
                                    textView.setText(text);
                                    notifyDataSetChanged();
                                    updateChange();
                                    alarmClockBean = null;
                                    return true;
                                }
                            })
                    .positiveText(R.string.choose)
                    .negativeText(R.string.cancel)
                    .show();

        }

        public void showDialogDeleteClock() {
            new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.warning))
                    .content(context.getString(R.string.deleteClock))
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //delete
                            dialog.dismiss();
                            alarmClockList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            if (onRemoveClockListener != null) {
                                onRemoveClockListener.onRemoveClockListener();
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }


    }

    private void updateChange() {
        if (haveChangeListener != null) {
            haveChangeListener.haveChangeListener();
        }
    }

    public interface OnRemoveClockListener {
        public void onRemoveClockListener();
    }

    public interface HaveChangeListener {
        public void haveChangeListener();
    }
}
