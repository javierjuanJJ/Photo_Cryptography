package com.example.photocryptography.asyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class DecodeAsyncTask extends AsyncTask<String, Void, Bitmap> {

   private final WeakReference<Context> contextReference;
   private final WeakReference<ImageView> imgReference;
   private final WeakReference<ProgressBar> copyPbReference;


   public DecodeAsyncTask(Context context, ImageView img, ProgressBar copy_pb) {
      this.contextReference = new WeakReference<Context>(context);
      this.imgReference = new WeakReference<ImageView>(img);
      this.copyPbReference = new WeakReference<ProgressBar>(copy_pb);
   }

   protected void onPostExecute(Bitmap bitmap) {
      Context context = contextReference.get();
      ProgressBar copyPb = copyPbReference.get();
      ImageView imgView = imgReference.get();

      imgView.setImageBitmap(bitmap);
      Toast.makeText(context, "Image desencrypted!", Toast.LENGTH_SHORT).show();

      copyPb.setVisibility(View.INVISIBLE);

   }

   protected Bitmap doInBackground(String... encodeContentFiles) {
      ProgressBar copyPb = copyPbReference.get();
      copyPb.setVisibility(View.INVISIBLE);

      byte[] bytes = Base64.decode(encodeContentFiles[0], Base64.DEFAULT);
      Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


      return bitmap;
   }
}
