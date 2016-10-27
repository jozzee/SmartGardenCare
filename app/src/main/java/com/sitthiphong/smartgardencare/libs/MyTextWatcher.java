package com.sitthiphong.smartgardencare.libs;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Sitthiphong on 10/18/2016 AD.
 */

public class MyTextWatcher implements TextWatcher {

    private TextInputLayout textInputLayout;
    public MyTextWatcher(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        textInputLayout.setErrorEnabled(false);

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
