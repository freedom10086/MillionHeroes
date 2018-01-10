package com.xdluoyang.millionheroes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int OVERLAY_PERMISSION_REQ_CODE = 1;
    private Button overlayBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        overlayBtn = findViewById(R.id.overlay);
        overlayBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    Toast.makeText(MainActivity.this, "请务必开启悬浮窗权限，否则无法查看", Toast.LENGTH_LONG).show();
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                }
            }
        });


        FloatWindowManager.createSmallWindow(getApplicationContext());
        FloatWindowManager.smallWindow.setOnClickListener(v -> {
            Log.i("===", "click");
            if (!FloatWindowManager.isBigWindowShowing()) {
                Log.i("===", "click1");
                FloatWindowManager.createBigWindow(getApplicationContext());
                FloatWindowManager.bigWindow.startSearch();
            } else {
                Log.i("===", "click1");
                FloatWindowManager.removeBigWindow(getApplicationContext());
            }
        });
    }

    private void updateUI() {
        boolean isok = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                isok = false;
            }
        }

        overlayBtn.setText(isok ? "悬浮窗权限:OK" : "点击开启悬浮窗权限");
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] != PermissionChecker.PERMISSION_GRANTED) {
            Toast.makeText(this, "需要权限读取/保存截图文件", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 666);
            return false;
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 666);
            return false;
        } else {
            return true;
        }
    }
}
