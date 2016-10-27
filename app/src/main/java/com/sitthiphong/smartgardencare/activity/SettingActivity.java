package com.sitthiphong.smartgardencare.activity;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.datamodel.ConfigData;
import com.sitthiphong.smartgardencare.libs.ShareData;
import com.sitthiphong.smartgardencare.listener.OnSaveSettingListener;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {
    private final String TAG = "SettingActivity";

    public static OnSaveSettingListener onSaveSettingListener;

    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private Menu menu;
    private ShareData shareData;
    private RelativeLayout appIdLayout, keyLayout, secretLayout, fqPDataLayout, fqPImageLayout,
            fqIDataLayout, fqShowerLayout, ageDataLayout, autoModeLayout;

    private TextView appId, key, secret, fqPData, fqPImage, fqIData, fqShower, ageData, autoMode;
    private Switch swAutoMode;

    private int fqPDataValue, fqPImageValue, fqIDataValue, fqShowerValue, ageDataValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        shareData = new ShareData(this);
        shareData.createSharePreference();

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);  //set RootLayout
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting);


        appIdLayout = (RelativeLayout) findViewById(R.id.app_id_layout);
        keyLayout = (RelativeLayout) findViewById(R.id.key_layout);
        secretLayout = (RelativeLayout) findViewById(R.id.secret_layout);
        fqPDataLayout = (RelativeLayout) findViewById(R.id.fqPData_layout);
        fqPImageLayout = (RelativeLayout) findViewById(R.id.fqPImage_layout);
        fqIDataLayout = (RelativeLayout) findViewById(R.id.fqIData_layout);
        fqShowerLayout = (RelativeLayout) findViewById(R.id.fqShower_layout);
        ageDataLayout = (RelativeLayout) findViewById(R.id.ageData_layout);
        autoModeLayout = (RelativeLayout) findViewById(R.id.auto_mode_layout);

        appId = (TextView) findViewById(R.id.app_id_value);
        key = (TextView) findViewById(R.id.key_value);
        secret = (TextView) findViewById(R.id.secret_value);
        fqPData = (TextView) findViewById(R.id.fqPData_value);
        fqPImage = (TextView) findViewById(R.id.fqPImage_value);
        fqIData = (TextView) findViewById(R.id.fqIData_value);
        fqShower = (TextView) findViewById(R.id.fqShower_value);
        ageData = (TextView) findViewById(R.id.ageData_value);
        autoMode = (TextView) findViewById(R.id.auto_mode_value);
        swAutoMode = (Switch) findViewById(R.id.swAutoMode);

        setValues();
    }

    private void setValues() {
        appId.setText(shareData.getAppId());
        key.setText(shareData.getAppKey());
        secret.setText(shareData.getAppSecret());
        key.setText(shareData.getAppKey());

        fqPDataValue = shareData.getFqPData();
        fqPData.setText(getString(R.string.every) + " "
                + String.valueOf(fqPDataValue) + " "
                + getString(R.string.minute));

        fqPImageValue = shareData.getFqPImage();
        fqPImage.setText(getString(R.string.every) + " "
                + String.valueOf(fqPImageValue) + " "
                + getString(R.string.hour));

        fqIDataValue = shareData.getFqIData();
        fqIData.setText(getString(R.string.every) + " "
                + String.valueOf(fqIDataValue) + " "
                + getString(R.string.hour));

        fqShowerValue = shareData.getFqShower();
        if (fqShowerValue == 0) {
            fqShower.setText(getString(R.string.accordingToTheSystem));
        } else {
            fqShower.setText(String.valueOf(fqShowerValue) + " " + getString(R.string.minute));
        }

        ageDataValue = shareData.getAgeData();
        ageData.setText(String.valueOf(ageDataValue) + " " + getString(R.string.days));

        swAutoMode.setChecked(shareData.isAutoMode());
        if (swAutoMode.isChecked()) {
            autoMode.setText(getString(R.string.on));
        } else {
            autoMode.setText(getString(R.string.off));
        }

        appIdLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtDialog(getString(R.string.appId), appId);
            }
        });
        keyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtDialog(getString(R.string.appKey), key);
            }
        });
        secretLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtDialog(getString(R.string.appSecret), secret);
            }
        });
        fqPDataLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog(getString(R.string.frequency) + " " + getString(R.string.publishData),
                        R.array.fqPDataArray,
                        fqPDataValue,
                        fqPData);
            }
        });
        fqPImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog(getString(R.string.frequency) + " " + getString(R.string.publishImage),
                        R.array.fqPImageArray,
                        fqPImageValue,
                        fqPImage);
            }
        });
        fqIDataLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog(getString(R.string.frequency) + " " + getString(R.string.insertData),
                        R.array.fqIDataArray,
                        fqIDataValue,
                        fqIData);

            }
        });

        fqShowerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog(getString(R.string.timeOfFoggy),
                        R.array.fqShowerArray,
                        fqShowerValue,
                        fqShower);
            }
        });
        ageDataLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog(getString(R.string.ageData),
                        R.array.ageDataArray,
                        ageDataValue,
                        ageData);
            }
        });


        autoModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swAutoMode.isChecked()) {
                    swAutoMode.setChecked(false);
                    autoMode.setText(getString(R.string.off));
                } else {
                    swAutoMode.setChecked(true);
                    autoMode.setText(getString(R.string.on));
                }
                validChange();
            }
        });


    }


    private void edtDialog(String title, final TextView textView) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                .input(title, textView.getText().toString().trim(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        textView.setText(input.toString());
                        validChange();
                    }
                }).show();
    }

    private void listDialog(String title, final int arrId, int values, final TextView textView) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .items(arrId)
                .itemsCallbackSingleChoice(getIndicatorFromValue(values, arrId),
                        new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                setValue(getValueFromIndicator(which, arrId), arrId);
                                textView.setText(text);
                                validChange();
                                return true;
                            }
                        })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        menu.findItem(R.id.actionSaveSetting).setVisible(false);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if (id == 16908332) {
            onBackPressed();
            return true;
        }
        if (id == R.id.actionSaveSetting) {
            Log.e(TAG, "onSaveSetting");
            onSaveSetting();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSaveSetting() {
        JsonObject objDetails = new JsonObject();
        JsonObject objNetPie = new JsonObject();

        if (!(appId.getText().toString().trim().equals(shareData.getAppId()))) {
            objNetPie.addProperty(ConfigData.appId, appId.getText().toString().trim());
        }
        if (!(key.getText().toString().trim().equals(shareData.getAppKey()))) {
            objNetPie.addProperty(ConfigData.key, key.getText().toString().trim());
        }
        if (!(secret.getText().toString().trim().equals(shareData.getAppSecret()))) {
            objNetPie.addProperty(ConfigData.secret, secret.getText().toString().trim());
        }

        if(fqPDataValue != shareData.getFqPData()){
            objDetails.addProperty(ConfigData.fqPData,fqPDataValue);
        }
        if(fqPImageValue != shareData.getFqPImage()){
            objDetails.addProperty(ConfigData.fqPImage,fqPImageValue);
        }
        if(fqIDataValue != shareData.getFqIData()){
            objDetails.addProperty(ConfigData.fqIData,fqIDataValue);
        }
        if(fqShowerValue != shareData.getFqShower()){
            objDetails.addProperty(ConfigData.fqShower,fqShowerValue);
        }
        if(ageDataValue != shareData.getAgeData()){
            objDetails.addProperty(ConfigData.ageData,ageDataValue);
        }
        if(swAutoMode.isChecked() != shareData.isAutoMode()){
            objDetails.addProperty(ConfigData.autoMode,swAutoMode.isChecked());
        }

        if(onSaveSettingListener != null){
            onSaveSettingListener.onSaveSettingListener(objNetPie,objDetails);
        }




    }


    private Context getContext() {
        return this;
    }

    private boolean validChange() {
        boolean result = true;
        if (shareData.getAppId().equals(appId.getText().toString().trim())) {
            if (shareData.getAppKey().equals(key.getText().toString().trim())) {
                if (shareData.getAppSecret().equals(secret.getText().toString().trim())) {
                    if (shareData.getFqPData() == fqPDataValue) {
                        if (shareData.getFqPImage() == fqPImageValue) {
                            if (shareData.getFqIData() == fqIDataValue) {
                                if (shareData.getFqPImage() == fqPImageValue) {
                                    if (shareData.getFqShower() == fqShowerValue) {
                                        if (shareData.getAgeData() == ageDataValue) {
                                            if (shareData.isAutoMode() == swAutoMode.isChecked()) {
                                                menu.findItem(R.id.actionSaveSetting).setVisible(false);
                                                result = false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (result) {
            menu.findItem(R.id.actionSaveSetting).setVisible(true);
        }
        return result;
    }

    private int getIndicatorFromValue(int value, int arrId) {
        int result = 0;
        if (arrId == R.array.fqPDataArray) {
            if (value == 1) {
                result = 0;
            } else if (value == 5) {
                result = 1;
            } else if (value == 10) {
                result = 2;
            } else if (value == 20) {
                result = 3;
            } else if (value == 30) {
                result = 4;
            }
        } else if (arrId == R.array.fqPImageArray) {
            if (value == 1) {
                result = 0;
            } else if (value == 2) {
                result = 1;
            } else if (value == 3) {
                result = 2;
            } else if (value == 6) {
                result = 3;
            } else if (value == 12) {
                result = 4;
            }
        } else if (arrId == R.array.fqIDataArray) {
            if (value == 1) {
                result = 0;
            } else if (value == 2) {
                result = 1;
            } else if (value == 3) {
                result = 2;
            } else if (value == 6) {
                result = 3;
            } else if (value == 12) {
                result = 4;
            }
        } else if (arrId == R.array.fqShowerArray) {
            if (value == 0) {
                result = 0;
            } else if (value == 1) {
                result = 1;
            } else if (value == 5) {
                result = 2;
            } else if (value == 10) {
                result = 3;
            } else if (value == 15) {
                result = 4;
            } else if (value == 20) {
                result = 4;
            }
        } else if (arrId == R.array.ageDataArray) {
            if (value == 1) {
                result = 0;
            } else if (value == 2) {
                result = 1;
            } else if (value == 3) {
                result = 2;
            }
        }

        return result;

    }

    private int getValueFromIndicator(int indicator, int arrId) {
        int result = 1;
        if (arrId == R.array.fqPDataArray) {
            if (indicator == 0) {
                result = 1;
            } else if (indicator == 1) {
                result = 5;
            } else if (indicator == 2) {
                result = 10;
            } else if (indicator == 3) {
                result = 20;
            } else if (indicator == 4) {
                result = 30;
            }
        } else if ((arrId == R.array.fqPImageArray) || (arrId == R.array.fqIDataArray)) {
            if (indicator == 0) {
                result = 1;
            } else if (indicator == 1) {
                result = 2;
            } else if (indicator == 2) {
                result = 3;
            } else if (indicator == 3) {
                result = 6;
            } else if (indicator == 4) {
                result = 12;
            }
        } else if (arrId == R.array.fqShowerArray) {
            if (indicator == 0) {
                result = 0;
            } else if (indicator == 1) {
                result = 1;
            } else if (indicator == 2) {
                result = 5;
            } else if (indicator == 3) {
                result = 10;
            } else if (indicator == 4) {
                result = 15;
            } else if (indicator == 5) {
                result = 20;
            }
        } else if (arrId == R.array.ageDataArray) {
            if (indicator == 0) {
                result = 1;
            } else if (indicator == 1) {
                result = 2;
            } else if (indicator == 2) {
                result = 3;
            }
        }
        return result;
    }


    private void setValue(int value, int arrId) {
        if (arrId == R.array.fqPDataArray) {
            fqPDataValue = value;
        } else if (arrId == R.array.fqPImageArray) {
            fqPImageValue = value;
        } else if (arrId == R.array.fqIDataArray) {
            fqIDataValue = value;
        } else if (arrId == R.array.fqShowerArray) {
            fqShowerValue = value;
        } else if (arrId == R.array.ageDataArray) {
            ageDataValue = value;
        }
    }


}
