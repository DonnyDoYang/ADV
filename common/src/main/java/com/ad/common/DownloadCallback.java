package com.ad.common;

/**
 * Created by wmmeng on 15/9/23.
 */
public interface DownloadCallback {
    void downloadSuccess(String path);
    void downloadFailed();
}
