package com.thresholdsoft.apollofeedback.utils.fileupload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

public class FileUploadRequest {

    @SerializedName("Filename")
    @Expose
    private File filename;

    public File getFilename() {
        return filename;
    }

    public void setFilename(File filename) {
        this.filename = filename;
    }
}
