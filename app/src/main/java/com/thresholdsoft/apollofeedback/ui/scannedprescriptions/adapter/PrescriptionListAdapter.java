package com.thresholdsoft.apollofeedback.ui.scannedprescriptions.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.databinding.AdapterPrescriptionListBinding;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.ScannedPrescriptionsActivityCallback;

import java.io.File;
import java.util.List;

public class PrescriptionListAdapter extends RecyclerView.Adapter<PrescriptionListAdapter.ViewHolder> {
    private Context context;
    private List<String> prescriptionPathList;
    private ScannedPrescriptionsActivityCallback mListener;

    public PrescriptionListAdapter(Context context, List<String> prescriptionPathList, ScannedPrescriptionsActivityCallback mListener) {
        this.context = context;
        this.prescriptionPathList = prescriptionPathList;
        this.mListener = mListener;
    }

    public void setPrescriptionPathList(List<String> prescriptionPathList) {
        this.prescriptionPathList = prescriptionPathList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterPrescriptionListBinding prescriptionListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_prescription_list, parent, false);
        return new ViewHolder(prescriptionListBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String prescriptionPath = prescriptionPathList.get(position);
        File imgFile = new File(prescriptionPath + "/1.jpg");
        if (imgFile.exists()) {
            Uri uri = Uri.fromFile(imgFile);
            holder.prescriptionListBinding.prescriptionViewPager.setImageURI(uri);
        }
        holder.prescriptionListBinding.prescriptionViewPager.setOnClickListener(v -> {
            if (mListener != null) mListener.onClickPrescription(prescriptionPath);
        });
        holder.prescriptionListBinding.itemCount.setText(String.valueOf(position + 1));
        holder.prescriptionListBinding.itemDelete.setOnClickListener(v -> {
            if (mListener != null) mListener.onClickItemDelete(position);
        });
    }

    @Override
    public int getItemCount() {
        return prescriptionPathList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AdapterPrescriptionListBinding prescriptionListBinding;

        public ViewHolder(@NonNull AdapterPrescriptionListBinding prescriptionListBinding) {
            super(prescriptionListBinding.getRoot());
            this.prescriptionListBinding = prescriptionListBinding;
        }
    }
}
