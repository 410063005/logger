package com.orhanobut.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Push;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class UploadLogActivity extends Activity {

    public static void start(Context context) {
        Intent starter = new Intent(context, UploadLogActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_log);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                push(null);
            } else {
                Toast.makeText(this, "无文件权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addMoreLog(View view) {
        Logger.i("log log log ");
        for (int i = 0; i < 4000; i++) {
            Logger.i("a very long log log log ");
        }
    }

    public void push(View view) {
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            new Push().upload();
        } else {
            requestPermissions(
                    new String[]{WRITE_EXTERNAL_STORAGE},
                    1000
            );
        }
    }

    public void showLogs(View view) {
        Logger.d(new Push().getLogFile());
    }
}
