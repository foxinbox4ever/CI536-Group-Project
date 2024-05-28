package com.example.aiphotoscanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    final String email = "a.musa3@uni.brighton.ac.uk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        String aboutMeText = getString(R.string.about_me_text);
        String email = "a.musa3@uni.brighton.ac.uk";
        String finalText = aboutMeText + email;

        TextView aboutMeTextView = findViewById(R.id.text_view_id);
        Button copyEmailButton = findViewById(R.id.copy_email);

        aboutMeTextView.setText(finalText);

        copyEmailButton.setOnClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("email", email);
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(AboutUsActivity.this, "Email copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        ImageView homeImageView = findViewById(R.id.home);
        homeImageView.setOnClickListener(view -> {
            returnToHome();
            Toast.makeText(AboutUsActivity.this, "You clicked the home button", Toast.LENGTH_SHORT).show();
        });
    }

    public void returnToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
