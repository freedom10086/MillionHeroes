package com.xdluoyang.millionheroes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class WindowBig extends RelativeLayout {

    //private TabLayout tab;
    private WebView webView;

    public static int bigViewWidth;
    public static int bigViewHeight;
    private int currentIndex = -1;
    private Result recResult;
    long start = 0;

    public WindowBig(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.window_big, this);

        webView = findViewById(R.id.webview);
        findViewById(R.id.close_btn).setOnClickListener(v -> FloatWindowManager.removeBigWindow(context));
        /*
        tab = findViewById(R.id.tab);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentIndex = tab.getPosition();
                getBaiduSearchResult(currentIndex, words.get(currentIndex));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        */


        View view = findViewById(R.id.big_window_layout);
        bigViewWidth = view.getLayoutParams().width;
        bigViewHeight = view.getLayoutParams().height;
    }


    public void startSearch() {
        start = System.currentTimeMillis();
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes("system/bin/screencap -p /sdcard/test/out.png\n");
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            p.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        // 裁剪图片
        String file1 = Environment.getExternalStorageDirectory() + "/test/out.png";
        String file2 = Environment.getExternalStorageDirectory() + "/test/small.png";

        Bitmap bmp = BitmapFactory.decodeFile(file1);
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int x = 0;
        for (; x < width / 4; x++) {
            int color = bmp.getPixel(x, height / 3);
            int red = (color & 0xff0000) >> 16;
            int green = (color & 0x00ff00) >> 8;
            int blue = (color & 0x0000ff);
            int avg = (red + green + blue) / 3;
            if (avg > 220) {
                break;
            }
        }

        int maxY = height - height / 4;
        for (; maxY > height / 2; maxY--) {
            int color = bmp.getPixel(width - 2 * x - 10, maxY);
            int red = (color & 0xff0000) >> 16;
            int green = (color & 0x00ff00) >> 8;
            int blue = (color & 0x0000ff);
            int avg = (red + green + blue) / 3;
            if (avg > 220) {
                break;
            }
            //Log.i("color", "r:" + red + " g:" + green + " b:" + blue + "avg:" + (red + green + blue) / 3);
        }

        Bitmap resizedbitmap = Bitmap.createBitmap(bmp, x + 20, height / 7 + 20, width - 2 * x - 40, maxY - height / 7 - 40);
        try {
            resizedbitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file2)); // bmp is your Bitmap instance
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }


        // 通用文字识别参数设置
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true);
        param.setImageFile(new File(file2));

        // 调用通用文字识别服务
        OCR.getInstance().recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                Result r = new Result();

                // 调用成功，返回GeneralResult对象
                for (WordSimple wordSimple : result.getWordList()) {
                    String word = wordSimple.getWords();
                    Log.i("==", word);

                    int index = word.indexOf(".");
                    if ((index == 1 || index == 2) && Pattern.matches("([0-9]{1,2})", word.substring(0, index))) {
                        r.addResult(word.substring(index + 1));
                    } else {
                        r.addResult(word);
                    }
                }

                recResult = r;
                Log.i("识别结果", r.toString());

                //调用百度搜索
                getBaiduSearchResult(0, r.getTitle());
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
                Log.i("result", error.toString());
            }
        });
    }

    private void getBaiduSearchResult(int index, String s) {
        Log.i("search", s);
        OkHttp.get(s, (success, s1) -> {
            if (!success) return;
            //if (index != currentIndex) return;
            Document document = Jsoup.parse(s1);
            document.select("#head").remove();
            document.select("#s_tab").remove();
            document.select("#content_right").remove();
            document.select("#page").remove();
            document.select("#foot").remove();
            Element e2 = document.select("#content_left").first();
            if (e2 != null)

                e2.attr("style", "width:100%;padding:5px");

            Elements els = document.select("#content_left .c-container");
            for (Element e : els) {
                Log.w("result title", e.select("h3").text());
                Log.w("result abstract", e.select(".c-abstract").text());
                Log.w("result", "--");
            }

            Log.i("time", (System.currentTimeMillis() - start) + "ms");

            if (success) {
                webView.loadDataWithBaseURL("http://www.baidu.com/", document.html(), "text/html;charset=utf-8", "utf-8", null);
            }
        });
    }

}
