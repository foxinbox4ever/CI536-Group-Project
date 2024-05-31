package com.example.aiphotoscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 101;
    private ImageView imgButton;
    private ImageView galleryButton;
    private ImageView capturedImageView;
    private Button uploadButton;
    private Button cancelButton;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;
    private Uri capturedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgButton = findViewById(R.id.imageButton);
        galleryButton = findViewById(R.id.galleryButton);
        capturedImageView = findViewById(R.id.capturedImageView);
        uploadButton = findViewById(R.id.uploadButton);
        cancelButton = findViewById(R.id.cancelButton);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        capturedImageUri = getImageUri(this, imageBitmap);
                        capturedImageView.setImageBitmap(imageBitmap);
                        capturedImageView.setVisibility(View.VISIBLE);
                        uploadButton.setVisibility(View.VISIBLE);
                        cancelButton.setVisibility(View.VISIBLE);
                        imgButton.setVisibility(View.GONE);
                        galleryButton.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                    }
                });

        pickMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        capturedImageUri = uri;
                        capturedImageView.setImageURI(uri);
                        capturedImageView.setVisibility(View.VISIBLE);
                        uploadButton.setVisibility(View.VISIBLE);
                        cancelButton.setVisibility(View.VISIBLE);
                        imgButton.setVisibility(View.GONE);
                        galleryButton.setVisibility(View.GONE);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        imgButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                launchCamera();
            } else {
                requestCameraPermission();
            }
        });

        galleryButton.setOnClickListener(v -> {
            if (checkGalleryPermission()) {
                launchGallery();
            } else {
                requestGalleryPermission();
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

        if (savedInstanceState != null) {
            Uri uri = savedInstanceState.getParcelable("capturedImageUri");
            if (uri != null) {
                capturedImageUri = uri;
                capturedImageView.setImageURI(uri);
                capturedImageView.setVisibility(View.VISIBLE);
                uploadButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                imgButton.setVisibility(View.GONE);
                galleryButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("capturedImageUri", capturedImageUri);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private boolean checkGalleryPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
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
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGallery();
            } else {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
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
        try {
            cameraLauncher.launch(takePictureIntent);
        } catch (Exception e){
            Toast.makeText(this, "An error has happened", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchGallery() {
        try {
            PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build();

            pickMediaLauncher.launch(request);
        } catch (Exception e) {
            Toast.makeText(this, "An error has happened", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetView() {
        capturedImageView.setVisibility(View.GONE);
        uploadButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        imgButton.setVisibility(View.VISIBLE);
        galleryButton.setVisibility(View.VISIBLE);
        capturedImageUri = null;
    }

    private Uri getImageUri(Activity activity, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "Captured Image", null);
        return Uri.parse(path);
    }
}
