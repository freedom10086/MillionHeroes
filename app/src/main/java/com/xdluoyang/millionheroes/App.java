package com.xdluoyang.millionheroes;

import android.app.Application;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
//            @Override
//            public void onResult(AccessToken result) {
//                // 调用成功，返回AccessToken对象
//                String token = result.getAccessToken();
//            }
//
//            @Override
//            public void onError(OCRError error) {
//                // 调用失败，返回OCRError子类SDKError对象
//            }
//        }, getApplicationContext(), "您的应用AK", "您的应用SK");

        // 用api.license初始化
        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
            }
            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
            }
        }, getApplicationContext());
    }
}
