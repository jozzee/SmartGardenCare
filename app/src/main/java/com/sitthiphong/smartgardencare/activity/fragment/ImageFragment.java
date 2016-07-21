package com.sitthiphong.smartgardencare.activity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.bean.ImageBean;
import com.sitthiphong.smartgardencare.bean.StatusBean;
import com.sitthiphong.smartgardencare.core.MagScreen;
import com.sitthiphong.smartgardencare.listener.ActionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {
    private String TAG = "ImageFragment";
    private View rootView;
    private ImageView imageGarden;
    private CoordinatorLayout rootLayout;
    private ProgressBar progressBar;
    private TextView exception;

    private ActionListener actionListener = new ActionListener();
    private OnFragmentInteractionListener mListener;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance() {
        ImageFragment fragment = new ImageFragment();
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
        rootView = inflater.inflate(R.layout.fragment_image, container, false);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        MagScreen magScreen = new MagScreen(getActivity(),metrics);

        imageGarden = (ImageView)rootView.findViewById(R.id.imGarden);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                magScreen.getWidthGardenImage(),magScreen.getHeightGardenImage());
        imageGarden.setLayoutParams(param);

        imageGarden.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.bg_garden_im));

        rootLayout = (CoordinatorLayout) rootView.findViewById(R.id.containImage);
        rootLayout.setVisibility(View.GONE);

        exception = (TextView)rootView.findViewById(R.id.exceptionImage);
        exception.setVisibility(View.GONE);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBarImage);
        progressBar.setVisibility(View.VISIBLE);

        setActionListener();




        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        actionListener.onRequestUpdateImage.onRequestUpdateImage();

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
    public void setActionListener(){
        actionListener.setOnException(new ActionListener.OnException() {
            @Override
            public void onException(String error) {
                Log.e(TAG,"onException");
                rootLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                exception.setText(error);
                exception.setVisibility(View.VISIBLE);
            }
        });
        actionListener.setOnUpdateImage(new ActionListener.OnUpdateImage() {
            @Override
            public void onUpdateImage(StatusBean statusBean, ImageBean imageBean) {
                if(statusBean.getStatus() == getActivity().getResources().getInteger(R.integer.IS_CONNECT_NETPIE)){
                    //ถ้าคอนเน็คเสร็จ เมื่อขออัพเดทไปแล้ว แต่มันยังเสือกไม่มี จะไปโหลดข้อมผผูลโดยใช้ Asynctask  พอได้ข้อมูล
                    // เมื่อได้ข้อม฿ลมา ก็จะนำข้อมูลไป update view และส่งข้อมูลไปเก็ยที่ mainactivity
                }
                else if (statusBean.getStatus() == getActivity().getResources().getInteger(R.integer.ERROR)){
                    rootLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    exception.setText(statusBean.getException());
                    exception.setVisibility(View.VISIBLE);

                }
                else if(statusBean.getStatus() == getActivity().getResources().getInteger(R.integer.NO_INTERNET)){

                }
            }
        });
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
