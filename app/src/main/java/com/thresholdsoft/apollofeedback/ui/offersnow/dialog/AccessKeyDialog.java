package com.thresholdsoft.apollofeedback.ui.offersnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.databinding.AccessKeyDialogBinding;

public class AccessKeyDialog {

    private Dialog dialog;
    private AccessKeyDialogBinding accesskeyBinding;
    private Context context;


    public AccessKeyDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        accesskeyBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.access_key_dialog, null, false);
        dialog.setContentView(accesskeyBinding.getRoot());
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        accesskeyBinding.accesskeyTextinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() <= 1) {
                    accesskeyBinding.accesskeyErrorBg.setBackground(null);
                    accesskeyBinding.accesskeyErrorText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        accesskeyBinding.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = dialog.getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void onClickSubmit(View.OnClickListener onSubmitListener) {
        accesskeyBinding.submit.setOnClickListener(onSubmitListener);
    }

    public void listener() {
        if (validate()) {
//            SessionManager.INSTANCE.setAccessKey(accesskeyBinding.accesskeyTextinput.getText().toString().trim());
            Toast.makeText(context, "You Submitted", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validate() {
        if (!accesskeyBinding.accesskeyTextinput.getText().toString().trim().equals("AP@11@2021")) {
            accesskeyBinding.accesskeyErrorBg.setBackground(context.getResources().getDrawable(R.drawable.phone_error_alert_bg));
            accesskeyBinding.accesskeyErrorText.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }
}
