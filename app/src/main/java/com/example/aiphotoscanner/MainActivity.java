package com.example.aiphotoscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private ImageView imgButton;
    private ImageView capturedImageView;
    private Button uploadButton;
    private Button cancelButton;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgButton = findViewById(R.id.imageButton);
        capturedImageView = findViewById(R.id.capturedImageView);
        uploadButton = findViewById(R.id.uploadButton);
        cancelButton = findViewById(R.id.cancelButton);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        capturedImageView.setImageBitmap(imageBitmap);
                        capturedImageView.setVisibility(View.VISIBLE);
                        uploadButton.setVisibility(View.VISIBLE);
                        cancelButton.setVisibility(View.VISIBLE);
                        imgButton.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                    }
                });

        imgButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                launchCamera();
            } else {
                requestCameraPermission();
            }
        });

        uploadButton.setOnClickListener(v -> {
            // add code to send photo to server
            Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show();
            resetView();
        });

        cancelButton.setOnClickListener(v -> {
            resetView();
        });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_item, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.aboutus) {
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                Toast.makeText(this, "You have clicked about us", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetView() {
        capturedImageView.setVisibility(View.GONE);
        uploadButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        imgButton.setVisibility(View.VISIBLE);
    }
}
