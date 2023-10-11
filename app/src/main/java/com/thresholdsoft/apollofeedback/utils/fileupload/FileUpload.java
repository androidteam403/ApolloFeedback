package com.thresholdsoft.apollofeedback.utils.fileupload;

import android.content.Context;
import android.widget.Toast;

import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.utils.AppConstants;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;
import com.thresholdsoft.apollofeedback.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUpload {
    private Context context;
    private FileUploadCallback fileUploadCallback;
    private List<FileUploadModel> fileUploadModelList;

    public void uploadFiles(Context context, FileUploadCallback fileUploadCallback, List<FileUploadModel> fileUploadModelList) {
        this.context = context;
        this.fileUploadCallback = fileUploadCallback;
        this.fileUploadModelList = fileUploadModelList;

        if (NetworkUtils.isNetworkConnected(context)) {
//            showLoading(context !!)
            uploadFile(fileUploadModelList.get(0));
        } else {
            fileUploadCallback.onFailureUpload("Something went wrong.");
        }
    }

    public void uploadFile(FileUploadModel fileUploadModel) {
        CommonUtils.showDialog(context, "Please wait");

        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFilename(fileUploadModel.getFile());

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), fileUploadModel.getFile());
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", fileUploadModel.getFile().getName(), requestBody);

        ApiInterface apiInterface = ApiClient.getApiService("https://online.apollopharmacy.org/UAT/OrderPlace.svc/");
        Call<FileUploadResponse> call = apiInterface.FILE_UPLOAD_API_CALL(AppConstants.FILE_UPLOAD_URL_UAT, "multipart/form-data", AppConstants.FILE_UPLOAD_TOKEN_UAT, fileToUpload);

        call.enqueue(new Callback<FileUploadResponse>() {
            @Override
            public void onResponse(@NotNull Call<FileUploadResponse> call, @NotNull Response<FileUploadResponse> response) {
                CommonUtils.hideDialog();
                if (response.body() != null && response.body().isStatus()) {
                    onSuccessFileUpload(fileUploadModel, response.body());
                } else {
                    onFailureFileUpload(fileUploadModel, response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<FileUploadResponse> call, @NotNull Throwable t) {
                CommonUtils.hideDialog();
                fileUploadCallback.onFailureUpload(t.getMessage());
            }
        });
    }

    public void onSuccessFileUpload(FileUploadModel fileUploadModel, FileUploadResponse fileUploadResponse) {
        fileUploadModel.setFileUploaded(true);
        fileUploadModel.setFileUploadResponse(fileUploadResponse);
        FileUploadModel fileUploadModelTemp = null;
        for (FileUploadModel fum : fileUploadModelList) {
            if (!fum.isFileUploaded()) {
                fileUploadModelTemp = fum;
                break;
            }
        }
        if (fileUploadModelTemp != null) {
            uploadFile(fileUploadModelTemp);
        } else {
            CommonUtils.hideDialog();
//            fileUploadCallback.allFilesUploaded(fileUploadModelList);
            downloadFiles(context, fileUploadCallback, fileUploadModelList);
        }
    }

    public void onFailureFileUpload(FileUploadModel fileUploadModel, FileUploadResponse fileUploadResponse) {
        CommonUtils.hideDialog();
        Toast.makeText(context, "File upload failed", Toast.LENGTH_SHORT).show();
    }


    public void downloadFiles(Context context, FileUploadCallback fileUploadCallback, List<FileUploadModel> fileUploadModelList) {
        this.context = context;
        this.fileUploadCallback = fileUploadCallback;
        this.fileUploadModelList = fileUploadModelList;

        if (NetworkUtils.isNetworkConnected(context)) {
            CommonUtils.showDialog(context, "Please wait");
            downloadFile(fileUploadModelList.get(0));
        } else {
            fileUploadCallback.onFailureUpload("Something went wrong.");
        }
    }


    public void downloadFile(FileUploadModel fileUploadModel) {
        CommonUtils.showDialog(context, "Please wait");

        FileDownloadRequest fileDownloadRequest = new FileDownloadRequest();
        fileDownloadRequest.setRefURL(fileUploadModel.getFileUploadResponse().getReferenceurl());

        ApiInterface apiInterface = ApiClient.getApiService("https://online.apollopharmacy.org/UAT/OrderPlace.svc/");
        Call<FileDownloadResponse> call = apiInterface.FILE_DOWNLOAD_API_CALL(AppConstants.FILE_DOWNLOAD_URL_UAT, AppConstants.FILE_DOWNLOAD_TOEKN_UAT, fileDownloadRequest);

        call.enqueue(new Callback<FileDownloadResponse>() {
            @Override
            public void onResponse(@NotNull Call<FileDownloadResponse> call, @NotNull Response<FileDownloadResponse> response) {
                CommonUtils.hideDialog();
                if (response.body() != null && response.body().isStatus()) {
                    onSuccessFileDownload(fileUploadModel, response.body());
                } else {
                    onFailureFileDownload(fileUploadModel, response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<FileDownloadResponse> call, @NotNull Throwable t) {
                CommonUtils.hideDialog();
                fileUploadCallback.onFailureUpload(t.getMessage());
            }
        });
    }


    public void onSuccessFileDownload(FileUploadModel fileUploadModel, FileDownloadResponse fileDownloadResponse) {
        fileUploadModel.setFileDownloaded(true);
        fileUploadModel.setFileDownloadResponse(fileDownloadResponse);
        FileUploadModel fileUploadModelTemp = null;
        for (FileUploadModel fum : fileUploadModelList) {
            if (!fum.isFileDownloaded()) {
                fileUploadModelTemp = fum;
                break;
            }
        }
        if (fileUploadModelTemp != null) {
            downloadFile(fileUploadModelTemp);
        } else {
            CommonUtils.hideDialog();
            try {
                fileUploadCallback.allFilesDownloaded(fileUploadModelList);
            } catch (Exception e) {
                System.out.println("onSuccessFileDownload ::::::: FileUpload");
            }

        }
    }

    public void onFailureFileDownload(FileUploadModel fileUploadModel, FileDownloadResponse fileDownloadResponse) {
        CommonUtils.hideDialog();
        Toast.makeText(context, "File download failed", Toast.LENGTH_SHORT).show();
    }
}
