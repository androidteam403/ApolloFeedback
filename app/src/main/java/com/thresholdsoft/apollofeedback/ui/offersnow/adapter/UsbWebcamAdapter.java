package com.thresholdsoft.apollofeedback.ui.offersnow.adapter;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.databinding.AdapterUsbWebcamBinding;
import com.thresholdsoft.apollofeedback.ui.offersnow.OffersNowActivityCallback;

import java.util.List;

public class UsbWebcamAdapter extends RecyclerView.Adapter<UsbWebcamAdapter.ViewHolder> {

    private Context mContext;
    private List<UsbDevice> needNotifyDevices;
    private OffersNowActivityCallback mCallback;

    public UsbWebcamAdapter(Context mContext, List<UsbDevice> needNotifyDevices, OffersNowActivityCallback mCallback) {
        this.mContext = mContext;
        this.needNotifyDevices = needNotifyDevices;
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public UsbWebcamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterUsbWebcamBinding adapterUsbWebcamBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_usb_webcam, parent, false);
        return new ViewHolder(adapterUsbWebcamBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsbWebcamAdapter.ViewHolder holder, int position) {
        holder.adapterUsbWebcamBinding.usbCamRadioBtn.setText(needNotifyDevices.get(position).getDeviceName());
        holder.itemView.setOnClickListener(v -> {
            mCallback.onSelectWebCam(needNotifyDevices.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return needNotifyDevices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AdapterUsbWebcamBinding adapterUsbWebcamBinding;

        public ViewHolder(@NonNull AdapterUsbWebcamBinding adapterUsbWebcamBinding) {
            super(adapterUsbWebcamBinding.getRoot());
            this.adapterUsbWebcamBinding = adapterUsbWebcamBinding;
        }
    }
}
