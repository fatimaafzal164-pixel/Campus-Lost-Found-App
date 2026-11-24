package com.example.campuslostfound;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    TextView tvDetailTitle, tvDetailLocation, tvDetailDesc;
    Button btnContactFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailDesc = findViewById(R.id.tvDetailDesc);
        btnContactFinder = findViewById(R.id.btnContactFinder);

        String title = getIntent().getStringExtra("title");
        String location = getIntent().getStringExtra("location");
        String desc = getIntent().getStringExtra("desc");

        tvDetailTitle.setText(title);
        tvDetailLocation.setText("Location: " + location);
        tvDetailDesc.setText(desc);

        btnContactFinder.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Campus Lost & Found: " + title);
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Assalam-o-Alaikum, I saw your post about '" + title + "' at " + location + ".");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send Email via..."));
            } catch (Exception e) {
                Toast.makeText(this, "No email app found on device", Toast.LENGTH_SHORT).show();
            }
        });
    }
}