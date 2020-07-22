package com.example.NoteProject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.TextView;

import com.example.NoteProject.Adapters.ImageGalleryAdapter;
import com.example.NoteProject.Utils.PermissionUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageActivity extends AppCompatActivity {


    @BindView(R.id.image_gallery_rv)
    RecyclerView imageGalleryRv;
    @BindView(R.id.heading)
    TextView heading;
    private Context mContext;
    private ArrayList image_path;
    private String path;
    private File file;
    private Uri uri;
    private int CAMERA_REQUEST = 67;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        mContext = ImageActivity.this;

        if (PermissionUtils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                && PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            getAllShownImagesPath();
        } else {
            PermissionUtils.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);
        }

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {

            if (resultCode == Activity.RESULT_OK) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("uri", uri.toString());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        } else if (resultCode == RESULT_OK && requestCode == 12) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        }

    }

    private void getAllShownImagesPath() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID + " DESC";

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);

        int count = cursor.getCount();
        image_path = new ArrayList();
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            image_path.add(cursor.getString(dataColumnIndex));
        }
        setAdapter();
        cursor.close();
    }

    private void setAdapter() {
        ImageGalleryAdapter imageGalleryAdapter = new ImageGalleryAdapter(this, image_path);
        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        imageGalleryRv.setLayoutManager(staggeredGridLayoutManager);
        imageGalleryRv.setAdapter(imageGalleryAdapter);
    }


}
