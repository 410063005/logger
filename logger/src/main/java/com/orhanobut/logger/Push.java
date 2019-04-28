package com.orhanobut.logger;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class Push {
    private static final String URL = "http://142.93.81.218:8000/log/";
    private static final String REFERER = "http://142.93.81.218:8000/log/";

    private final File folder;
    private OkHttpClient httpClient;

    public Push() {
        String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        folder = new File(diskPath + File.separatorChar + "logger");
    }

    public void upload() {
        List<File> files =  getLogFile();
        for (int i = 0; i < files.size(); i++) {
            // Don't delete the last one, maybe it is busy now
            boolean deleteAfterUpload = (i < files.size() - 1);

            File file = files.get(i);
            try {
                uploadFile(file, deleteAfterUpload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile(final File file, final boolean deleteAfterUpload) throws IOException {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(null, file)).build();
        Request request = new Request.Builder().url(URL).post(formBody)
                .header("referer", REFERER)
                .build();

        getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (response.code() == 200 && deleteAfterUpload) {
                    if (file.delete()) {
                        System.out.println("Delete log " + file);
                    }
                }
                if (body != null) {
                    System.out.println(body.string());
                    body.close();
                }
            }
        });
    }

    public List<File> getLogFile() {
        final String fileName = "logs";
        List<File> files = new ArrayList<>();

        File folder = this.folder;
        if (!folder.exists()) {
            //TODO: What if folder is not created, what happens then?
            folder.mkdirs();
        }

        int newFileCount = 0;
        File newFile;
        File existingFile;

        newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
        while (newFile.exists()) {
            existingFile = newFile;
            files.add(existingFile);
            newFileCount++;
            newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
        }

        return files;
    }

    private OkHttpClient getHttpClient() {
        if (httpClient == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
        }
        return httpClient;
    }


}
