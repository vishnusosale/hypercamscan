package com.hyper.hypercamscan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 100;
    private static final int REQUEST_CODE = 99;
    private static final String TAG = MainActivity.class.getCanonicalName();

    private ImageView resultImageView;
    private Button takePictureButton;

    private boolean isPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultImageView = findViewById(R.id.result_image_view);
        takePictureButton = findViewById(R.id.take_picture_button);

        if (checkLocationPermission()) {
            isPermissionGranted = true;
            takePictureButton.setText(getString(R.string.take_picture));
        } else {
            takePictureButton.setText(getString(R.string.provide_permissions));
        }

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPermissionGranted) {
                    openCamera();
                } else {
                    requestForPermission();
                }
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkLocationPermission() {

        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0) {
                boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean writeExternalStorage = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                if (cameraPermission && readExternalStorage && writeExternalStorage) {
                    // write your logic here
                    isPermissionGranted = true;
                    openCamera();
                } else {
                    isPermissionGranted = false;
                    takePictureButton.setText(getString(R.string.provide_permissions));
                }
            }
        }
    }
}
