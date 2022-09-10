package com.codefullness.memeverse;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    Button next, save;
    String memeUrl;
    private static int REQUEST_CODE=1;
    boolean isDank, isIndianDank, isMeyMeys, isMeme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        iv = findViewById(R.id.imageview);
        next = findViewById(R.id.nextbtn);
        save = findViewById(R.id.savebtn);

        // Asking for permission
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_CODE);

        // Calling fetchDankMemes
        fetchDankMemes();
        isDank = true;
        isIndianDank = false;
        isMeyMeys = false;
        isMeme = false;

        // Next Meme
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDank)
                    fetchDankMemes();
                else if (isIndianDank)
                    fetchIndianDankMemes();
                else if (isMeyMeys)
                    fetchIndianMeyMeys();
                else if (isMeme)
                    fetchMeme();
            }
        });

        // Button to download memes
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save Method

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(memeUrl));
                String title = URLUtil.guessFileName(memeUrl, null, null);
                request.setTitle(title);
                request.setDescription("Downloading Meme");
                String cookie = CookieManager.getInstance().getCookie(memeUrl);
                request.addRequestHeader("cookie",cookie);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);

                Toast.makeText(MainActivity.this, "Download Started", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (isDank) {
            if (item.getItemId() == R.id.indiandankmemes){
                fetchIndianDankMemes();
                isIndianDank = true;
                isDank = false;
                isMeyMeys = false;
                isMeme = false;
            }

            if (item.getItemId() == R.id.indianmeymeys){
                fetchIndianMeyMeys();
                isMeyMeys = true;
                isDank = false;
                isIndianDank = false;
                isMeme = false;
            }

            if (item.getItemId() == R.id.memes){
                fetchMeme();
                isMeme = true;
                isDank = false;
                isIndianDank = false;
                isMeyMeys = false;
            }
        }

        if (isIndianDank) {
            if (item.getItemId() == R.id.dankmemes){
                fetchDankMemes();
                isDank = true;
                isIndianDank = false;
                isMeyMeys = false;
                isMeme = false;
            }
            if (item.getItemId() == R.id.indianmeymeys){
                fetchIndianMeyMeys();
                isMeyMeys = true;
                isDank = false;
                isIndianDank = false;
                isMeme = false;
            }
            if (item.getItemId() == R.id.memes){
                fetchMeme();
                isMeme = true;
                isDank = false;
                isIndianDank = false;
                isMeyMeys = false;
            }
        }

        if (isMeyMeys){
            if (item.getItemId() == R.id.dankmemes){
                fetchDankMemes();
                isDank = true;
                isIndianDank = false;
                isMeyMeys = false;
                isMeme = false;
            }
            if (item.getItemId() == R.id.indiandankmemes){
                fetchIndianDankMemes();
                isIndianDank = true;
                isDank = false;
                isMeyMeys = false;
                isMeme = false;
            }
            if (item.getItemId() == R.id.memes){
                fetchMeme();
                isMeme = true;
                isDank = false;
                isIndianDank = false;
                isMeyMeys = false;
            }
        }

        if (isMeme){
            if (item.getItemId() == R.id.dankmemes){
                fetchDankMemes();
                isDank = true;
                isIndianDank = false;
                isMeyMeys = false;
                isMeme = false;
            }
            if (item.getItemId() == R.id.indiandankmemes){
                fetchIndianDankMemes();
                isIndianDank = true;
                isDank = false;
                isMeyMeys = false;
                isMeme = false;
            }
            if (item.getItemId() == R.id.indianmeymeys){
                fetchIndianMeyMeys();
                isMeyMeys = true;
                isDank = false;
                isIndianDank = false;
                isMeme = false;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    public void fetchDankMemes(){
        Call<MemeResponse> memeResponseCall;
        memeResponseCall = MyApi.getApiService().getDankMemes();
        memeResponseCall.enqueue(new Callback<MemeResponse>() {
            @Override
            public void onResponse(Call<MemeResponse> call, Response<MemeResponse> response) {
                memeUrl = response.body().getUrl();
                Glide.with(MainActivity.this)
                        .load(memeUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(iv);
            }

            @Override
            public void onFailure(Call<MemeResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchMeme(){
        Call<MemeResponse> memeResponseCall;
        memeResponseCall = MyApi.getApiService().getMeme();
        memeResponseCall.enqueue(new Callback<MemeResponse>() {
            @Override
            public void onResponse(Call<MemeResponse> call, Response<MemeResponse> response) {
                memeUrl = response.body().getUrl();
                Glide.with(MainActivity.this)
                        .load(memeUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(iv);
            }

            @Override
            public void onFailure(Call<MemeResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchIndianDankMemes(){
        Call<MemeResponse> memeResponseCall;
        memeResponseCall = MyApi.getApiService().getIndianDankMemes();
        memeResponseCall.enqueue(new Callback<MemeResponse>() {
            @Override
            public void onResponse(Call<MemeResponse> call, Response<MemeResponse> response) {
                memeUrl = response.body().getUrl();
                Glide.with(MainActivity.this)
                        .load(memeUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(iv);
            }

            @Override
            public void onFailure(Call<MemeResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchIndianMeyMeys(){
        Call<MemeResponse> memeResponseCall;
        memeResponseCall = MyApi.getApiService().getIndianMeyMeys();
        memeResponseCall.enqueue(new Callback<MemeResponse>() {
            @Override
            public void onResponse(Call<MemeResponse> call, Response<MemeResponse> response) {
                memeUrl = response.body().getUrl();
                Glide.with(MainActivity.this)
                        .load(memeUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(iv);
            }

            @Override
            public void onFailure(Call<MemeResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}