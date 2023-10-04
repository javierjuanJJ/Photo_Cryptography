package com.example.photocryptography;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.photocryptography.databinding.ActivityPhotoCryptographyBinding;

public class PhotoCryptography extends AppCompatActivity {
    private Button encrypt, decrypt;
    String image;
    ClipboardManager clipboardManager;
    ImageView imgView;
    EditText encImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_cryptography);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Crypt Image");
        actionBar.show();

        encrypt = findViewById(R.id.enc_btn);
        decrypt = findViewById(R.id.dec_btn);
        encImg = findViewById(R.id.enc_txt);

        encImg.setEnabled(false);
        imgView = findViewById(R.id.imgView);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);


        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(PhotoCryptography.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoCryptography.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }
                else {
                    selectPhoto();
                }
            }
        });

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    byte[] bytes = Base64.decode(encImg.getText().toString(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgView.setImageBitmap(bitmap);
                }
            }
        });

    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select picture"), 100);

    }
}