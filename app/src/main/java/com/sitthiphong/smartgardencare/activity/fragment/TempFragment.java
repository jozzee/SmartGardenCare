package com.sitthiphong.smartgardencare.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.activity.MoreActivity;
import com.sitthiphong.smartgardencare.datamodel.RawDataBean;
import com.sitthiphong.smartgardencare.datamodel.RawDataBeanList;
import com.sitthiphong.smartgardencare.core.MagDiscreteSeekBar;
import com.sitthiphong.smartgardencare.core.MagPieView;
import com.sitthiphong.smartgardencare.core.MagScreen;
import com.sitthiphong.smartgardencare.core.linechart.MagLineChart;
import com.sitthiphong.smartgardencare.listener.ActionListener;
import com.sitthiphong.smartgardencare.provider.GsonProvider;
import com.sitthiphong.smartgardencare.provider.SimpleDateProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TempFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TempFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempFragment extends Fragment {
    private String TAG = "TempFragment";
    private OnFragmentInteractionListener mListener;
    private ActionListener actionListener = new ActionListener();
    private View rootView;
    private Button btnShower;
    private TextView lastTime,sensorTitle1,sensorTitle2,sensorValue1,sensorValue2,tempValue,more,exception,sensorError;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MagPieView pieView;
    private MagDiscreteSeekBar seekBar;
    private MagLineChart lineChart;
    private NestedScrollView scrollView;
    private RelativeLayout layoutContainLinChart;
    private ProgressBar progressBar;
    private MagScreen screen;

    public TempFragment() {
        // Required empty public constructor
    }
    public static TempFragment newInstance() {
        TempFragment fragment = new TempFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        sharedPreferences = getActivity().getSharedPreferences("Details", getActivity().MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView");

        rootView =  inflater.inflate(R.layout.fragment_temp, container, false);

        sensorError = (TextView)rootView.findViewById(R.id.sensorError);

        pieView = new MagPieView(
                getActivity(),
                rootView,
                R.id.pieView,
                -1,
                getActivity().getResources().getString(R.string.unitTemp),
                ContextCompat.getColor(getActivity(),R.color.deepOrange));

        btnShower = (Button)rootView.findViewById(R.id.btnAction);
        btnShower.setText(getActivity().getResources().getString(R.string.shower));
        btnShower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onControlDevice.onControlDevice(2,true);
            }
        });

        lastTime = (TextView)rootView.findViewById(R.id.time_value);
        //lastTime.setText( new SimpleDateFormat("HH:mm dd-MM-yyyy",java.util.Locale.US)
        //        .format(new Date(rawDataBean.getTime()*1000)));

        sensorTitle1 = (TextView)rootView.findViewById(R.id.sensor1_title);
        sensorTitle2 = (TextView)rootView.findViewById(R.id.sensor2_title);
        sensorTitle1.setText(getResources().getString(R.string.tempSensor1));
        sensorTitle2.setText(getResources().getString(R.string.tempSensor2));

        sensorValue1 = (TextView)rootView.findViewById(R.id.sensor1_value);
        sensorValue2 = (TextView)rootView.findViewById(R.id.sensor2_value);

        tempValue = (TextView)rootView.findViewById(R.id.value_standard);
        tempValue.setText(String.valueOf((int)sharedPreferences.getFloat("temp",40))+" °C");
        seekBar = new MagDiscreteSeekBar(
                rootView,
                R.id.seekBarValue,
                tempValue,
                getActivity().getResources().getString(R.string.unitTemp),//unit
                ContextCompat.getColor(getActivity(),R.color.deepOrange),//color
                50,//max
                10,//min
                40,
                "dht22");//progress
        seekBar.createSeekBar();

        more = (TextView)rootView.findViewById(R.id.more_raw_data);
        more.setTextColor(ContextCompat.getColor(getActivity(),R.color.deepOrange));


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screen = new MagScreen(getActivity(),metrics);

//        //------------------------------------------------------------------------------------------
//        String payload = "1467477960,20,-1,-1";
//        RawDataBean rawDataBean = new RawDataBean(payload);
//
//        String test = "[{\"topic\":\"/ECPSmartGarden/rawDataList\",\"payload\":\"[{\\\"humidity\\\": 57.78, \\\"id\\\": 230, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1467028800\\\", \\\"light\\\": 79.16},{\\\"humidity\\\": 57.78, \\\"id\\\": 229, \\\"temp\\\": 25.3, \\\"time\\\": \\\"1467025200\\\", \\\"light\\\": 77.5},{\\\"humidity\\\": 57.78, \\\"id\\\": 228, \\\"temp\\\": 25.3, \\\"time\\\": \\\"1467021602\\\", \\\"light\\\": 70.0},{\\\"humidity\\\": 57.78, \\\"id\\\": 227, \\\"temp\\\": 24.5, \\\"time\\\": \\\"1467018000\\\", \\\"light\\\": 78.33},{\\\"humidity\\\": 57.78, \\\"id\\\": 226, \\\"temp\\\": 23.0, \\\"time\\\": \\\"1467014401\\\", \\\"light\\\": 79.16},{\\\"humidity\\\": 57.78, \\\"id\\\": 225, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1466960402\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": -1.0, \\\"id\\\": 224, \\\"temp\\\": 28.8, \\\"time\\\": \\\"1466956800\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": 21.0, \\\"id\\\": 223, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466935200\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": 21.0, \\\"id\\\": 222, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466931601\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": 21.0, \\\"id\\\": 221, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466928000\\\", \\\"light\\\": 9.16},{\\\"humidity\\\": 21.0, \\\"id\\\": 220, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466924400\\\", \\\"light\\\": 7.5},{\\\"humidity\\\": 21.0, \\\"id\\\": 219, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466920800\\\", \\\"light\\\": 20.0},{\\\"humidity\\\": 21.0, \\\"id\\\": 218, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466917201\\\", \\\"light\\\": 26.66},{\\\"humidity\\\": 21.0, \\\"id\\\": 217, \\\"temp\\\": 31.4, \\\"time\\\": \\\"1466913602\\\", \\\"light\\\": 31.66},{\\\"humidity\\\": 21.0, \\\"id\\\": 216, \\\"temp\\\": 29.3, \\\"time\\\": \\\"1466877600\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": 21.0, \\\"id\\\": 215, \\\"temp\\\": 29.2, \\\"time\\\": \\\"1466874002\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": 21.0, \\\"id\\\": 214, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466794800\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 213, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466791200\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 212, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466787601\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 211, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466784000\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 210, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466780400\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 209, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466776800\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 208, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466773200\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 207, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1466769601\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 206, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1466766001\\\", \\\"light\\\": 5.0},{\\\"humidity\\\": 21.0, \\\"id\\\": 205, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466762402\\\", \\\"light\\\": 10.0},{\\\"humidity\\\": 21.0, \\\"id\\\": 204, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466758801\\\", \\\"light\\\": 24.166},{\\\"humidity\\\": 21.0, \\\"id\\\": 203, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1466683202\\\", \\\"light\\\": 18.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 202, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1466679600\\\", \\\"light\\\": 21.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 201, \\\"temp\\\": 26.9, \\\"time\\\": \\\"1466676001\\\", \\\"light\\\": 12.5},{\\\"humidity\\\": 21.0, \\\"id\\\": 200, \\\"temp\\\": 26.9, \\\"time\\\": \\\"1466672401\\\", \\\"light\\\": 12.5},{\\\"humidity\\\": 21.0, \\\"id\\\": 199, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466618400\\\", \\\"light\\\": 6.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 198, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466614801\\\", \\\"light\\\": 5.833},{\\\"humidity\\\": 21.0, \\\"id\\\": 197, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466611202\\\", \\\"light\\\": 5.833},{\\\"humidity\\\": 21.0, \\\"id\\\": 196, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466607602\\\", \\\"light\\\": 6.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 195, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466604000\\\", \\\"light\\\": 6.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 194, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466600400\\\", \\\"light\\\": 6.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 193, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466596801\\\", \\\"light\\\": 6.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 192, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466593202\\\", \\\"light\\\": 10.0},{\\\"humidity\\\": 21.0, \\\"id\\\": 191, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466578802\\\", \\\"light\\\": 31.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 190, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466575202\\\", \\\"light\\\": 60.833},{\\\"humidity\\\": 21.0, \\\"id\\\": 189, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466571602\\\", \\\"light\\\": 73.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 188, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466568000\\\", \\\"light\\\": 53.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 187, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466528401\\\", \\\"light\\\": 8.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 186, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466524801\\\", \\\"light\\\": 8.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 185, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466521200\\\", \\\"light\\\": 8.333}]\",\"lastUpdated\":1467476755,\"retain\":true}]";
//        Gson gson = new Gson();
//        SubscribeBean subscribeBean = new SubscribeBean(test);
//        JsonArray jsonArray = gson.fromJson(subscribeBean.getPayload(), JsonArray.class);
//        Type listType = new TypeToken<ArrayList<RawDataBean>>(){}.getType();
//        List<RawDataBean> rawList = gson.fromJson(jsonArray, listType);
        //------------------------------------------------------------------------------------------

        layoutContainLinChart = (RelativeLayout)rootView.findViewById(R.id.layoutContainLinChart);
        layoutContainLinChart.setVisibility(View.GONE);

        lineChart = new MagLineChart(
                getActivity(),
                rootView,
                R.id.lineChart,
                2, //type chart 1=moisture,2=temp,3=light
                null);
        //lineChart.setRawList(rawList);
        if(lineChart.getRawList()!=null){
            lineChart.createLineChart(screen);
            lineChart.drawLineChart();
        }
        scrollView = (NestedScrollView)rootView.findViewById(R.id.scrollViewTemp);
        scrollView.setVisibility(View.GONE);

        exception = (TextView)rootView.findViewById(R.id.exceptionTemp);
        exception.setVisibility(View.GONE);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBarTemp);
        progressBar.setVisibility(View.VISIBLE);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moreIntent = new Intent(getContext(),MoreActivity.class);
                moreIntent.putExtra("from",2);
                moreIntent.putExtra("rawListBean",GsonProvider.getInstance().toJson(lineChart.getRawList()));
                startActivity(moreIntent);
            }
        });


        setActionListener();

        return  rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        //actionListener.onRequestRawData.OnRequestRawData();
        actionListener.onRequestRawBean.onRequestRawBean();
        actionListener.onRequestRawList.onRequestRawList();

    }
    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }
    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();

    }
    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }

    public void setActionListener(){
        actionListener.setOnException(new ActionListener.OnException() {
            @Override
            public void onException(String error) {
                Log.e(TAG,"onException");
                scrollView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                exception.setText(error);
                exception.setVisibility(View.VISIBLE);
            }
        });
//        actionListener.setOnUpdateRawData(new ActionListener.OnUpdateRawData() {
//            @Override
//            public void OnUpdateRawDat(StatusBean statusBean, RawDataBean rawDataBean) {
//                if(statusBean.getStatus() == getActivity().getResources().getInteger(R.integer.IS_CONNECT_NETPIE)){
//
//                }
//                else if (statusBean.getStatus() == getActivity().getResources().getInteger(R.integer.ERROR)){
//                    scrollView.setVisibility(View.GONE);
//                    progressBar.setVisibility(View.GONE);
//                    exception.setText(statusBean.getException());
//                    exception.setVisibility(View.VISIBLE);
//
//                }
//                else if(statusBean.getStatus() == getActivity().getResources().getInteger(R.integer.NO_INTERNET)){
//
//                }
//            }
//        });
        actionListener.setOnUpdateRawBean(new ActionListener.OnUpdateRawBean() {
            @Override
            public void onUpdateRawBean(RawDataBean rawBean) {

                if(rawBean.getTempAverage()>0){
                    exception.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    sensorError.setVisibility(View.GONE);
                    pieView.setValue(rawBean.getTempAverage());
                    pieView.setVisibility(View.VISIBLE);

                }
                else{
                    exception.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    pieView.setVisibility(View.GONE);
                    sensorError.setText(getResources().getString(R.string.errorSensorDHT22));//getResources().getString(R.string.errorSensorDHT22)
                    sensorError.setVisibility(View.VISIBLE);


                }
                lastTime.setText(SimpleDateProvider.getInstance()
                        .format(new Date(rawBean.getTime()*1000)));

                if(rawBean.getTemp1()>0){
                    sensorValue1.setText(String.format( "%.2f", rawBean.getTemp1() ) +
                            getResources().getString(R.string.unitTemp));
                }
                else{
                    sensorValue1.setText(getResources().getString(R.string.errorSensorDHT22));
                }

                if(rawBean.getTemp2() >0 ){
                    sensorValue2.setText(String.format( "%.2f", rawBean.getTemp2() )+
                            getResources().getString(R.string.unitTemp));
                }
                else{
                    sensorValue2.setText(getResources().getString(R.string.errorSensorDHT22));
                }
                seekBar.setProgress((int)sharedPreferences.getFloat("temp",40));
                scrollView.setVisibility(View.VISIBLE);


            }
        });
        actionListener.setOnUpdateRawList(new ActionListener.OnUpdateRawList() {
            @Override
            public void onUpdateRawList(String rawListAsJsonString) {
                lineChart.setRawList(getRawList(rawListAsJsonString));
                if(lineChart.getRawList()!=null){
                    lineChart.createLineChart(screen);
                    lineChart.drawLineChart();
                }
                layoutContainLinChart.setVisibility(View.VISIBLE);
            }
        });

        actionListener.setOnSetStandardFalse(new ActionListener.OnSetStandardFalse() {
            @Override
            public void onSetStandardFalse() {
                seekBar.setProgress((int)sharedPreferences.getFloat("temp",40));
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public List<RawDataBeanList> getRawList(String rawListAsJsonString){
        JsonArray jsonArray = GsonProvider.getInstance().fromJson(rawListAsJsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<RawDataBeanList>>(){}.getType();
        return GsonProvider.getInstance().fromJson(jsonArray, listType);
    }
}
