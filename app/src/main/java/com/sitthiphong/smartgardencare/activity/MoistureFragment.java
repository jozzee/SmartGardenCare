package com.sitthiphong.smartgardencare.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.bean.RawDataBean;
import com.sitthiphong.smartgardencare.bean.SubscribeBean;
import com.sitthiphong.smartgardencare.core.MagDiscreteSeekBar;
import com.sitthiphong.smartgardencare.core.MagLineChart;
import com.sitthiphong.smartgardencare.core.MagPieView;
import com.sitthiphong.smartgardencare.listener.SubscribeCallBackListener;
import com.sitthiphong.smartgardencare.core.MagScreen;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import az.plainpie.PieView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoistureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoistureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoistureFragment extends Fragment {
    private String TAG = "MoistureFragment";
    private View rootView;
    private OnFragmentInteractionListener mListener;
    private PieView pieView;
    private Button btnWater;
    private TextView lastTime;
    private TextView autoSwitchTitle;
    private Switch autoSwitch;
    private TextView more;

    public MoistureFragment() {
        // Required empty public constructor
    }

    public static MoistureFragment newInstance() {
        MoistureFragment fragment = new MoistureFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_moisture, container, false);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        MagScreen screen = new MagScreen(getActivity(),metrics);


        String payload = "1467477960,20,-1,-1";
        RawDataBean rawDataBean = new RawDataBean(payload);

        btnWater = (Button)rootView.findViewById(R.id.btnAction);
        btnWater.setText(getActivity().getResources().getString(R.string.water));
        lastTime = (TextView)rootView.findViewById(R.id.time_value);
        lastTime.setText( new SimpleDateFormat("HH:mm dd-MM-yyyy",java.util.Locale.US)
                .format(new Date(rawDataBean.getTime()*1000)));
        autoSwitchTitle = (TextView)rootView.findViewById(R.id.auto_title);
        autoSwitchTitle.setText(getActivity().getResources().getString(R.string.autoWater));
        autoSwitch = (Switch)rootView.findViewById(R.id.switchAuto);
        more = (TextView)rootView.findViewById(R.id.more_raw_data);
        more.setTextColor(ContextCompat.getColor(getActivity(),R.color.blue));





        //------------------------------------------------------------------------------------------

        String test = "[{\"topic\":\"/ECPSmartGarden/rawDataList\",\"payload\":\"[{\\\"humidity\\\": 57.78, \\\"id\\\": 230, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1467028800\\\", \\\"light\\\": 79.16},{\\\"humidity\\\": 57.78, \\\"id\\\": 229, \\\"temp\\\": 25.3, \\\"time\\\": \\\"1467025200\\\", \\\"light\\\": 77.5},{\\\"humidity\\\": 57.78, \\\"id\\\": 228, \\\"temp\\\": 25.3, \\\"time\\\": \\\"1467021602\\\", \\\"light\\\": 70.0},{\\\"humidity\\\": 57.78, \\\"id\\\": 227, \\\"temp\\\": 24.5, \\\"time\\\": \\\"1467018000\\\", \\\"light\\\": 78.33},{\\\"humidity\\\": 57.78, \\\"id\\\": 226, \\\"temp\\\": 23.0, \\\"time\\\": \\\"1467014401\\\", \\\"light\\\": 79.16},{\\\"humidity\\\": 57.78, \\\"id\\\": 225, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1466960402\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": -1.0, \\\"id\\\": 224, \\\"temp\\\": 28.8, \\\"time\\\": \\\"1466956800\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": 21.0, \\\"id\\\": 223, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466935200\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": 21.0, \\\"id\\\": 222, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466931601\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": 21.0, \\\"id\\\": 221, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466928000\\\", \\\"light\\\": 9.16},{\\\"humidity\\\": 21.0, \\\"id\\\": 220, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466924400\\\", \\\"light\\\": 7.5},{\\\"humidity\\\": 21.0, \\\"id\\\": 219, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466920800\\\", \\\"light\\\": 20.0},{\\\"humidity\\\": 21.0, \\\"id\\\": 218, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466917201\\\", \\\"light\\\": 26.66},{\\\"humidity\\\": 21.0, \\\"id\\\": 217, \\\"temp\\\": 31.4, \\\"time\\\": \\\"1466913602\\\", \\\"light\\\": 31.66},{\\\"humidity\\\": 21.0, \\\"id\\\": 216, \\\"temp\\\": 29.3, \\\"time\\\": \\\"1466877600\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": 21.0, \\\"id\\\": 215, \\\"temp\\\": 29.2, \\\"time\\\": \\\"1466874002\\\", \\\"light\\\": 3.33},{\\\"humidity\\\": 21.0, \\\"id\\\": 214, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466794800\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 213, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466791200\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 212, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466787601\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 211, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466784000\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 210, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466780400\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 209, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466776800\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 208, \\\"temp\\\": 30.6, \\\"time\\\": \\\"1466773200\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 207, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1466769601\\\", \\\"light\\\": 3.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 206, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1466766001\\\", \\\"light\\\": 5.0},{\\\"humidity\\\": 21.0, \\\"id\\\": 205, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466762402\\\", \\\"light\\\": 10.0},{\\\"humidity\\\": 21.0, \\\"id\\\": 204, \\\"temp\\\": 31.6, \\\"time\\\": \\\"1466758801\\\", \\\"light\\\": 24.166},{\\\"humidity\\\": 21.0, \\\"id\\\": 203, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1466683202\\\", \\\"light\\\": 18.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 202, \\\"temp\\\": -1.0, \\\"time\\\": \\\"1466679600\\\", \\\"light\\\": 21.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 201, \\\"temp\\\": 26.9, \\\"time\\\": \\\"1466676001\\\", \\\"light\\\": 12.5},{\\\"humidity\\\": 21.0, \\\"id\\\": 200, \\\"temp\\\": 26.9, \\\"time\\\": \\\"1466672401\\\", \\\"light\\\": 12.5},{\\\"humidity\\\": 21.0, \\\"id\\\": 199, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466618400\\\", \\\"light\\\": 6.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 198, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466614801\\\", \\\"light\\\": 5.833},{\\\"humidity\\\": 21.0, \\\"id\\\": 197, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466611202\\\", \\\"light\\\": 5.833},{\\\"humidity\\\": 21.0, \\\"id\\\": 196, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466607602\\\", \\\"light\\\": 6.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 195, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466604000\\\", \\\"light\\\": 6.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 194, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466600400\\\", \\\"light\\\": 6.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 193, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466596801\\\", \\\"light\\\": 6.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 192, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466593202\\\", \\\"light\\\": 10.0},{\\\"humidity\\\": 21.0, \\\"id\\\": 191, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466578802\\\", \\\"light\\\": 31.666},{\\\"humidity\\\": 21.0, \\\"id\\\": 190, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466575202\\\", \\\"light\\\": 60.833},{\\\"humidity\\\": 21.0, \\\"id\\\": 189, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466571602\\\", \\\"light\\\": 73.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 188, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466568000\\\", \\\"light\\\": 53.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 187, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466528401\\\", \\\"light\\\": 8.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 186, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466524801\\\", \\\"light\\\": 8.333},{\\\"humidity\\\": 21.0, \\\"id\\\": 185, \\\"temp\\\": 39.0, \\\"time\\\": \\\"1466521200\\\", \\\"light\\\": 8.333}]\",\"lastUpdated\":1467476755,\"retain\":true}]";
        Gson gson = new Gson();
        SubscribeBean subscribeBean = new SubscribeBean(test);
        JsonArray jsonArray = gson.fromJson(subscribeBean.getPayload(), JsonArray.class);
        Type listType = new TypeToken<ArrayList<RawDataBean>>(){}.getType();
        List<RawDataBean> rawList = gson.fromJson(jsonArray, listType);


        MagLineChart lineChart = new MagLineChart(getActivity(),rootView,R.id.lineChart,2,rawList);
        lineChart.createLineChart(screen);
        lineChart.drawLineChart();

        TextView textViewValue = (TextView)rootView.findViewById(R.id.value_standard);

        MagDiscreteSeekBar seekBar = new MagDiscreteSeekBar(
                rootView,
                R.id.seekBarValue,
                textViewValue,
                " %",//unit
                ContextCompat.getColor(getActivity(),R.color.blue),//color
                80,//max
                10,//min
                20);//progress
        seekBar.createSeekBar();

        MagPieView pieView = new MagPieView(
                getActivity(),
                rootView,
                R.id.pieView,
                rawDataBean.getHumidity(),
                " %",
                ContextCompat.getColor(getActivity(),R.color.blue));
        pieView.createPieView();


        return  rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

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
}
