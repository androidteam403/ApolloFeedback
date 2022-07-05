package com.thresholdsoft.apollofeedback.ui.storesetup.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.thresholdsoft.apollofeedback.R;
import com.thresholdsoft.apollofeedback.databinding.AdapterStoreListBinding;
import com.thresholdsoft.apollofeedback.ui.storesetup.model.StoreListResponseModel;

import java.util.ArrayList;

public class GetStoresListAdapter extends RecyclerView.Adapter<GetStoresListAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<StoreListResponseModel.StoreListObj> storeArrayList;
    private ArrayList<StoreListResponseModel.StoreListObj> storeFilteredArrayList;
    private GetStoresDialogMvpView storesDialogMvpView;
    AdapterStoreListBinding doctorSearchItemBinding;

    public GetStoresListAdapter(Context context, ArrayList<StoreListResponseModel.StoreListObj> storesArrList, GetStoresDialogMvpView getStoresDialogMvpView) {
        this.context = context;
        this.storeArrayList = storesArrList;
        this.storeFilteredArrayList = storesArrList;
        this.storesDialogMvpView = getStoresDialogMvpView;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        doctorSearchItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.adapter_store_list, parent, false);
        return new ViewHolder(doctorSearchItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoreListResponseModel.StoreListObj item = storeFilteredArrayList.get(position);
        holder.doctorSearchItemBinding.setModel(storeFilteredArrayList.get(position));
//        holder.doctorSearchItemBinding.medicineName.setText(item.getStoreName());
        holder.itemView.setOnClickListener(v -> {
            if (storesDialogMvpView != null) {
                storesDialogMvpView.onClickListener(item);
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public AdapterStoreListBinding doctorSearchItemBinding;

        public ViewHolder(@NonNull AdapterStoreListBinding doctorSearchItemBinding) {
            super(doctorSearchItemBinding.getRoot());
            this.doctorSearchItemBinding = doctorSearchItemBinding;
        }
    }

    @Override
    public int getItemCount() {
        return storeFilteredArrayList.size();
    }

    public void onClickListener(GetStoresDialogMvpView mvpView) {
        this.storesDialogMvpView = mvpView;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    storeFilteredArrayList = storeArrayList;
                } else {
                    ArrayList<StoreListResponseModel.StoreListObj> filteredList = new ArrayList<>();
                    for (StoreListResponseModel.StoreListObj row : storeArrayList) {
                        if (row.getStoreId().contains(charString.toUpperCase()) || row.getStoreName().contains(charString.toUpperCase())) {
                            filteredList.add(row);
                        }
                    }
                    storeFilteredArrayList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = storeFilteredArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                storeFilteredArrayList = (ArrayList<StoreListResponseModel.StoreListObj>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}
