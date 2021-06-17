package com.example.adressbook.NetworkTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


//          AsyncTask의 Generic 구성 : AsyncTask<Params, Progress, Result>
public class ImageNetworkTask extends AsyncTask<Integer, String, Integer> {

    final static String TAG = "MemberAddActivity";
    Context context = null;
    String mAddr = null;
    ProgressDialog progressDialog = null;
    String devicePath;
    ImageView imageView;

    public ImageNetworkTask(Context context, ImageView imageView,  String devicePath, String mAddr) {
        this.context = context;
        this.mAddr = mAddr;
        this.devicePath = devicePath;
        this.imageView = imageView;
    }

    @Override
    protected void onPreExecute() {
        Log.v(TAG, "onPreExecute()");
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Status");
        progressDialog.setMessage("Uploading ....");
        progressDialog.show();

    }

    @Override
    protected void onProgressUpdate(String... values) {
        Log.v(TAG, "onProgressUpdate()");
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        Log.v(TAG, "onPostExecute()");
        progressDialog.dismiss();

    }

    @Override
    protected void onCancelled() {
        Log.v(TAG,"onCancelled()");
        super.onCancelled();
    }


    //              File Upload 작업이 Background에서 동작함
    @Override
    protected Integer doInBackground(Integer... integers) {
        File file = new File(devicePath);
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                //              RequestBody.create의 parameter의 순서가 바뀜
                .addFormDataPart("image", file.getName(), RequestBody.create(file, MediaType.parse("image/jpeg")))


                .build();

        Request request = new Request.Builder()
                .url(mAddr)
                .post(requestBody)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            return 1;

        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }


} // -----
