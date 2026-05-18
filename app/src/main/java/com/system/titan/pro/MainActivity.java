package com.system.titan.pro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQ_CODE = 2026;
    private static final int MANAGE_STORAGE_REQ_CODE = 2027;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(getLayoutResourceId());
        checkAndRequestRuntimePermissions();
    }

    private int getLayoutResourceId() {
        return getResources().getIdentifier("activity_main", "layout", getPackageName());
    }

    private void checkAndRequestRuntimePermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            addPermissionIfMissing(permissionsNeeded, Manifest.permission.BLUETOOTH_CONNECT);
            addPermissionIfMissing(permissionsNeeded, Manifest.permission.BLUETOOTH_SCAN);
            addPermissionIfMissing(permissionsNeeded, Manifest.permission.BLUETOOTH_ADVERTISE);
        }
        addPermissionIfMissing(permissionsNeeded, Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            addPermissionIfMissing(permissionsNeeded, "android.permission.POST_NOTIFICATIONS");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            addPermissionIfMissing(permissionsNeeded, Manifest.permission.READ_EXTERNAL_STORAGE);
            addPermissionIfMissing(permissionsNeeded, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), PERMISSION_REQ_CODE);
        } else {
            checkSpecialPermissions();
        }
    }

    private void addPermissionIfMissing(List<String> list, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            list.add(permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            checkSpecialPermissions();
        }
    }

    private void checkSpecialPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
                    startActivityForResult(intent, MANAGE_STORAGE_REQ_CODE);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, MANAGE_STORAGE_REQ_CODE);
                }
            }
        }
    }
}
