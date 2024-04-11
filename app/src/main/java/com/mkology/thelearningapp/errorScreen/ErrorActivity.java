package com.mkology.thelearningapp.errorScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mkology.thelearningapp.HomeActivity;
import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.helper.ApplicationConstants;

public class ErrorActivity extends AppCompatActivity {

    private String errorType;
    private Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            errorType = intent.getStringExtra("errorType");
        }

        if (ApplicationConstants.INTERNET_ERROR.equals(errorType)) {
            setContentView(R.layout.error_screen_internet);
        } else if (ApplicationConstants.SERVER_ERROR.equals(errorType)) {
            setContentView(R.layout.error_screen_server);
        }

        retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }
}
