package com.thresholdsoft.apollofeedback.ui.storesetup.dialog;

import com.thresholdsoft.apollofeedback.ui.storesetup.model.StoreListResponseModel;

public interface GetStoresDialogMvpView {
    void dismissDialog();

    void onClickListener(StoreListResponseModel.StoreListObj item);
}
