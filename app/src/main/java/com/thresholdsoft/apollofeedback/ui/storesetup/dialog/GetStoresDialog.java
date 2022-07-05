package com.thresholdsoft.apollofeedback.ui.storesetup.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.databinding.GetStoresDialogBinding;
import com.thresholdsoft.apollofeedback.ui.storesetup.model.StoreListResponseModel;
import com.thresholdsoft.apollofeedback.ui.storesetup.StoreSetupActivityMvpView;

import java.util.ArrayList;

public class GetStoresDialog implements GetStoresDialogMvpView {
    GetStoresDialogBinding getStoresDialogBinding;
    private Dialog dialog;
    private Context context;
    private ArrayList<StoreListResponseModel.StoreListObj> storesArrList = new ArrayList<>();
    private StoreSetupActivityMvpView storeSetupMvpView;

    public GetStoresDialog(Context context) {
        dialog = new Dialog(context);
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        getStoresDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.get_stores_dialog, null, false);
        dialog.setContentView(getStoresDialogBinding.getRoot());
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
    }


    public void setStoreListArray(ArrayList<StoreListResponseModel.StoreListObj> storesArrList) {
        this.storesArrList = storesArrList;
        setUp();
    }

    private void setUp() {

        getStoresDialogBinding.setCallback(this);

        GetStoresListAdapter storesListAdapter = new GetStoresListAdapter(context, storesArrList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        getStoresDialogBinding.storesRecyclerView.setLayoutManager(mLayoutManager);
        storesListAdapter.onClickListener(this);
        getStoresDialogBinding.storesRecyclerView.setAdapter(storesListAdapter);
        getStoresDialogBinding.doctorNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                storesListAdapter.getFilter().filter(s);
            }
        });
    }


    public void setStoreDetailsMvpView(StoreSetupActivityMvpView detailsMvpView) {
        this.storeSetupMvpView = detailsMvpView;
    }

    @Override
    public void dismissDialog() {
        dialog.dismiss();
        storeSetupMvpView.dialogCloseListiner();
    }

    @Override
    public void onClickListener(StoreListResponseModel.StoreListObj item) {
        storeSetupMvpView.onSelectStore(item);
        dialog.dismiss();
        storeSetupMvpView.dialogCloseListiner();
    }

    public void show() {

        dialog.show();
    }
}

