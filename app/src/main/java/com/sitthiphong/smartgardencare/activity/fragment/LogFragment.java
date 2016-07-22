package com.sitthiphong.smartgardencare.activity.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.activity.adapter.LogDataAdapter;
import com.sitthiphong.smartgardencare.bean.LogDataBean;
import com.sitthiphong.smartgardencare.bean.StatusBean;
import com.sitthiphong.smartgardencare.bean.SubscribeBean;
import com.sitthiphong.smartgardencare.core.NetPieRestApi;
import com.sitthiphong.smartgardencare.listener.ActionListener;
import com.sitthiphong.smartgardencare.listener.SubscribeCallBackListener;
import com.sitthiphong.smartgardencare.provider.GsonProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LogFragment extends Fragment {
    private String TAG = "LogFragment";
    private OnFragmentInteractionListener mListener;
    private ActionListener actionListener = new ActionListener();
    private View rootView;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LogDataAdapter adapter;

    private ProgressBar progressBar;
    private TextView exception;

    public LogFragment() {
        // Required empty public constructor
    }


    public static LogFragment newInstance() {
        LogFragment fragment = new LogFragment();
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
        Log.i(TAG, "onAttach");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_log, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerViewLog);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.GONE);


        exception = (TextView)rootView.findViewById(R.id.exceptionLog);
        exception.setVisibility(View.GONE);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBarLog);
        progressBar.setVisibility(View.VISIBLE);

        setActionListener();

        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        actionListener.onRequestLog.onRequestLog();

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

    private void setActionListener(){
        Log.i(TAG, "setActionListener");
        actionListener.setOnConnectedToNETPIE(new ActionListener.OnConnectedToNETPIE() {
            @Override
            public void onConnectedToNETPIE() {

            }
        });
        actionListener.setOnException(new ActionListener.OnException() {
            @Override
            public void onException(String error) {
                progressBar.setVisibility(View.GONE);
                exception.setText(error);
                exception.setVisibility(View.VISIBLE);
            }
        });
        actionListener.setOnUpdateLog(new ActionListener.OnUpdateLog() {
            @Override
            public void onUpdateLog(StatusBean statusBean, String logListAsJsonString) {
                progressBar.setVisibility(View.GONE);
                exception.setVisibility(View.GONE);

                adapter = new LogDataAdapter(getActivity(),getLogList(logListAsJsonString));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public List<LogDataBean> getLogList(String logListAsJsonString){
        JsonArray jsonArray = GsonProvider.getInstance().fromJson(logListAsJsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<LogDataBean>>(){}.getType();
        return GsonProvider.getInstance().fromJson(jsonArray, listType);
    }

}
