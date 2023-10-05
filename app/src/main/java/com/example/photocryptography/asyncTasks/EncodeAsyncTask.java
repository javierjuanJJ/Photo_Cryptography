package com.example.photocryptography.asyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class EncodeAsyncTask extends AsyncTask<Uri, Void, String> {

   private final WeakReference<Context> contextReference;
   private final WeakReference<EditText> encImgReference;
   private final WeakReference<ProgressBar> copyPbReference;


   public EncodeAsyncTask(Context context, EditText etEncImg, ProgressBar copy_pb) {
      this.contextReference = new WeakReference<Context>(context);
      this.encImgReference = new WeakReference<EditText>(etEncImg);
      this.copyPbReference = new WeakReference<ProgressBar>(copy_pb);
   }

   protected void onPostExecute(String result) {
      EditText encImg = encImgReference.get();
      Context context = contextReference.get();
      ProgressBar copyPb = copyPbReference.get();

      encImg.setText(result);
      Toast.makeText(context, "Image encrypted! Click on Decrypt to restore", Toast.LENGTH_SHORT).show();

      copyPb.setVisibility(View.INVISIBLE);

   }

   protected String doInBackground(Uri... uris) {

      String image = "";
      Bitmap bitmap;
      ImageDecoder.Source source = null;
      Context context = contextReference.get();
      ProgressBar copyPb = copyPbReference.get();


      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
         copyPb.setVisibility(View.VISIBLE);
         source = ImageDecoder.createSource(context.getContentResolver(), uris[0]);

         try {
            bitmap = ImageDecoder.decodeBitmap(source);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();
            image = Base64.encodeToString(bytes, Base64.DEFAULT);
         } catch (IOException e) {
            Toast.makeText(context, "The image can not be encrypted.", Toast.LENGTH_SHORT).show();
         }
      }
      return image;
   }
}
