package com.sitthiphong.smartgardencare.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.adapter.LogDataAdapter;
import com.sitthiphong.smartgardencare.core.CheckPermission;
import com.sitthiphong.smartgardencare.datamodel.LogDataBean;
import com.sitthiphong.smartgardencare.datamodel.StatusBean;
import com.sitthiphong.smartgardencare.listener.ActionListener;
import com.sitthiphong.smartgardencare.provider.GsonProvider;
import com.sitthiphong.smartgardencare.provider.SimpleDateProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewLog);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.GONE);


        exception = (TextView) rootView.findViewById(R.id.exceptionLog);
        exception.setVisibility(View.GONE);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarLog);
        progressBar.setVisibility(View.VISIBLE);

        FloatingActionButton myFab = (FloatingActionButton) rootView.findViewById(R.id.btnFab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (new CheckPermission(getActivity()).checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //new SaveIMTask().execute(imBean.getBitmap());
                    createPdf();
                } else {
                    new CheckPermission(getActivity()).requestPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    actionListener.onCheckPermission.onCheckPermission("save2PDF", android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });
        actionListener.setOnPermissionResult(new ActionListener.OnPermissionResult() {
            @Override
            public void onPermissionResult(String method, boolean b) {
                if (method.equals("save2PDF") && b) {
                    //new SaveIMTask().execute(imBean.getBitmap());
                    createPdf();
                }

            }
        });

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

    private void setActionListener() {
        Log.i(TAG, "setActionListener");

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

                adapter = new LogDataAdapter(getActivity(), getLogList(logListAsJsonString));
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public List<LogDataBean> getLogList(String logListAsJsonString) {
        JsonArray jsonArray = GsonProvider.getInstance().fromJson(logListAsJsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<LogDataBean>>() {
        }.getType();
        return GsonProvider.getInstance().fromJson(jsonArray, listType);
    }

    public void createPdf() {
        try {
            File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "SmartGarden");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.i("LOG_TAG", "Pdf Directory created");
            }
            //Create time stamp
            Date date = new Date();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

            File myFile = new File(pdfFolder + "log Data " + timeStamp + ".pdf");

            OutputStream output = new FileOutputStream(myFile);
            Document document = new Document();
            Font font = null;
            try {
                font = new Font(BaseFont.createFont("THSarabunNew.ttf",
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED));

            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PdfWriter.getInstance(document, output);
            try {
                document.open();
                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                List<List<String>> dataSet = getData();
                for (List<String> record : dataSet) {
                    for (String field : record) {
                        table.addCell(field);
                    }
                }
                document.add(table);
            } finally {
                document.close();
            }


            showPdf(myFile);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        startActivity(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getData() {
        List<List<String>> data = new ArrayList<List<String>>();
        String[] tableTitleList = getResources().getStringArray(R.array.titleTable);
        data.add(Arrays.asList(tableTitleList));
        List<LogDataBean> logList = adapter.getLogList();
        for (int i = 0; i < logList.size(); i++) {
            List<String> dataLine = new ArrayList<String>();
            LogDataBean bean = logList.get(i);
            String[] tt = createArrData(bean);
            for (int j = 0; j < 6; j++) {
                dataLine.add(tt[j]);

            }
            data.add(dataLine);
        }
        return data;
    }

    private String[] createArrData(LogDataBean bean) {
        String[] arr = new String[6];
        arr[0] = String.valueOf(SimpleDateProvider.getInstance()
                .format(new Date(bean.getTime() * 1000)));
        arr[2] = (bean.getType() == 1) ? getString(R.string.typeActionAuto) : getString(R.string.typeActionManual);
        arr[3] = String.valueOf(bean.getValBefore());
        arr[4] = String.valueOf(bean.getValAfter());

        switch (bean.getAction()) {
            case 1:
                arr[1] = String.valueOf(getString(R.string.water));
                arr[5] = "";
                break;
            case 2:
                arr[1] = String.valueOf(getString(R.string.shower));
                arr[5] = "";
                break;
            case 3:
                arr[1] = getString(R.string.acOpenSlat);
                arr[5] = "";
                arr[3] = "";
                arr[4] = "";
                break;
            case 4:
                arr[1] = getString(R.string.acCloseSlat);
                arr[5] = "";
                break;
            case 5:
                arr[1] = getString(R.string.water);
                arr[5] = getString(R.string.waterFalse);
                break;
            case 6:
                arr[1] = getString(R.string.water);
                arr[5] = getString(R.string.noWateringArea);
                break;
            case 7:
                arr[1] = getString(R.string.shower);
                arr[5] = getString(R.string.waterFalse);
                break;
            case 8:
                arr[1] = getString(R.string.shower);
                arr[5] = getString(R.string.tempNotDecrease);
                break;
            case 9:
                arr[1] = getString(R.string.acCloseSlat);
                arr[5] = getString(R.string.lightInNotDecrease);
                break;
            default:
                arr[1] = "-";
        }

        return arr;
    }

    public void showPdf(File file) {
        Log.d("PDF", "showPdf");
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Log.d("pdf", intent.getAction());
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/pdf");
        //getActivity().startActivity(intent);
        startActivity(intent);

    }


}
