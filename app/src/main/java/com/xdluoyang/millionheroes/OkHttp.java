package com.xdluoyang.millionheroes;


import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp {
    private static final byte[] LOCKER = new byte[0];
    private static OkHttp mInstance;
    private OkHttpClient mOkHttpClient;

    private OkHttp() {
        okhttp3.OkHttpClient.Builder ClientBuilder = new okhttp3.OkHttpClient.Builder();
        ClientBuilder.readTimeout(5, TimeUnit.SECONDS);//读取超时
        ClientBuilder.connectTimeout(8, TimeUnit.SECONDS);//连接超时
        ClientBuilder.writeTimeout(10, TimeUnit.SECONDS);//写入超时
        mOkHttpClient = ClientBuilder.build();
    }

    public static OkHttp getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new OkHttp();
                }
            }
        }
        return mInstance;
    }

    public static void get(String kw, SearchCallBack callBack) {
        HttpUrl.Builder httpBuider = HttpUrl.parse("http://www.baidu.com/s").newBuilder();
        httpBuider.addQueryParameter("wd", kw);

        Request request = new Request.Builder()
                .url(httpBuider.build())
                .build();


        getInstance().mOkHttpClient.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callBack.onResult(false, e.toString()));
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String res = response.body().string();
                        mainHandler.post(() -> callBack.onResult(true, res));
                    } catch (IOException e) {
                        mainHandler.post(() -> callBack.onResult(false, e.toString()));
                    }
                } else {
                    mainHandler.post(() -> callBack.onResult(false, "empty"));
                }

            }
        });
    }

    interface SearchCallBack {
        void onResult(boolean success, String s);
    }
}
