package com.example.viz.nextagram;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.params.ClientPNames;

import java.io.File;

/**
 * Created by viz on 2015. 3. 27..
 */
public class FileDownloader {
    private final Context context;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public FileDownloader(Context context) {
        this.context = context;
    }

    public void downFile(String fileUrl, String fileName) {


        final File filePath = new File(context.getFilesDir().getPath() + "/" + fileName);

        Log.i("isFileExists", filePath.exists() + " " + filePath.getAbsolutePath());


        if (!filePath.exists()) {

            client.get(fileUrl, new FileAsyncHttpResponseHandler(context) {
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                    Log.i("FileDownloader", "fail: ");
                }

                @Override
                public void onSuccess(int i, Header[] headers, File file) {
                    Log.i("FileDownloader", "success responsePath: " + file.getAbsolutePath());
                    Log.i("FileDownloader", "success originalPath: " + filePath.getAbsolutePath());
                    file.renameTo(filePath);
                }
            });

        }

    }

}
