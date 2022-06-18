package com.novoda.merlin;

class BindCallbackManager extends MerlinCallbackManager<BindableInterface> {

    BindCallbackManager(Register<BindableInterface> register) {
        super(register);
    }

    void onMerlinBind(NetworkStatus networkStatus) {
        Logger.d("onBind");
        for (BindableInterface bindable : registerables()) {
            bindable.onBind(networkStatus);
        }
    }
}
