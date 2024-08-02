package io.xconn.excelfilereader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 2000; // Duration in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the splash screen layout
        setContentView(R.layout.activity_splash);

        // Optional: set your logo programmatically if not set in XML
        ImageView logo = findViewById(R.id.logoImageView);
        logo.setImageResource(R.drawable.your_logo); // Replace with your logo

        // Handler to start the FileSelectionActivity after the delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start FileSelectionActivity
                Intent mainIntent = new Intent(SplashActivity.this, FileSelectionActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
