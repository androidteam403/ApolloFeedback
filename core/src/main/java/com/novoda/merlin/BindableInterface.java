package com.novoda.merlin;

public interface BindableInterface extends Registerable {
    void onBind(NetworkStatus networkStatus);
}
