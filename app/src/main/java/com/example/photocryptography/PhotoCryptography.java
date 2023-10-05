package com.example.photocryptography;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.photocryptography.asyncTasks.DecodeAsyncTask;
import com.example.photocryptography.asyncTasks.EncodeAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PhotoCryptography extends AppCompatActivity {
    private Button encrypt, decrypt;
    String image;
    ClipboardManager clipboardManager;
    ImageView imgView;
    EditText encImg;
    private Bundle state;
    private EncodeAsyncTask encodeAsyncTask;
    private DecodeAsyncTask decodeAsyncTask;
    private ProgressBar copy_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_cryptography);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("Crypt Image");
            actionBar.show();
        }

        encrypt = findViewById(R.id.enc_btn);
        decrypt = findViewById(R.id.dec_btn);
        encImg = findViewById(R.id.enc_txt);

        // encImg.setEnabled(false);
        imgView = findViewById(R.id.imgView);

        copy_pb = findViewById(R.id.copy_pb);

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(PhotoCryptography.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoCryptography.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                } else {
                    selectPhoto();
                }
            }
        });

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cancelAsyncTask(decodeAsyncTask);
                    decodeAsyncTask = new DecodeAsyncTask(getApplicationContext(), imgView, copy_pb);
                    decodeAsyncTask.execute(encImg.getText().toString().trim());
                    decrypt.setEnabled(false);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPhoto();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            cancelAsyncTask(encodeAsyncTask);
            encodeAsyncTask = new EncodeAsyncTask(getApplicationContext(), encImg, copy_pb);
            encodeAsyncTask.execute(uri);
            encrypt.setEnabled(false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelAsyncTask(encodeAsyncTask);
        cancelAsyncTask(decodeAsyncTask);
    }

    private void cancelAsyncTask(AsyncTask encodeAsyncTask) {
        copy_pb.setVisibility(View.INVISIBLE);
        AsyncTask.Status status;
        if (encodeAsyncTask != null) {
            status = encodeAsyncTask.getStatus();
            Log.i("cancel", "Status: " + status);
            if ((status == AsyncTask.Status.RUNNING) || (status == AsyncTask.Status.PENDING)) {

                Log.i("cancel", "The buton has pressed while the task is running");
                encodeAsyncTask.cancel(true);
                Log.i("cancel", "The task have just cancelled.");
            }
        } else Log.i("cancel", "Not started");

    }
}