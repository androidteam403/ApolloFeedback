package com.thresholdsoft.apollofeedback.ui.storesetup.dialog;

import com.thresholdsoft.apollofeedback.ui.storesetup.model.StoreListResponseModel;

public interface GetStoresDialogCallback {
    void dismissDialog();

    void onClickListener(StoreListResponseModel.StoreListObj item);

    void noOrderFound(int count);
}
