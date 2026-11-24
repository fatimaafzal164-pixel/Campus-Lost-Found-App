package com.example.campuslostfound;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    EditText etItemTitle, etLocation, etDescription;
    RadioGroup radioGroupType;
    Button btnSubmitReport, btnSelectImage;
    ImageView ivItemImage;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etItemTitle = findViewById(R.id.etItemTitle);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        radioGroupType = findViewById(R.id.radioGroupType);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivItemImage = findViewById(R.id.ivItemImage);

        btnSelectImage.setOnClickListener(v -> openFileChooser());

        btnSubmitReport.setOnClickListener(v -> {
            String title = etItemTitle.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            int selectedId = radioGroupType.getCheckedRadioButtonId();
            RadioButton selectedRb = findViewById(selectedId);
            String status = selectedRb.getText().toString().toUpperCase();

            if (title.isEmpty() || location.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSubmitReport.setEnabled(false);

            String userEmail = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getEmail() : "Campus Student";
            String encodedImage = "";
            if (imageUri != null) {
                encodedImage = encodeImageToBase64(imageUri);
            }

            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("title", title);
            itemMap.put("location", location);
            itemMap.put("description", desc);
            itemMap.put("status", status);
            itemMap.put("imageBase64", encodedImage);
            itemMap.put("userEmail", userEmail);
            itemMap.put("timestamp", System.currentTimeMillis());

            db.collection("Items").add(itemMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Item Reported Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSubmitReport.setEnabled(true);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private String encodeImageToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

            // Image ko chhota aur compress karna taake Firestore mein asani se save ho
            int maxWidth = 400;
            int maxHeight = (originalBitmap.getHeight() * maxWidth) / originalBitmap.getWidth();
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, maxWidth, maxHeight, true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] bytes = baos.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivItemImage.setImageURI(imageUri);
        }
    }
}