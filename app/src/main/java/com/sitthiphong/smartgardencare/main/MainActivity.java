package com.sitthiphong.smartgardencare.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sitthiphong.smartgardencare.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContain,
                        new ImageFragment().newInstance(),
                        "im").commit();
    }
}
