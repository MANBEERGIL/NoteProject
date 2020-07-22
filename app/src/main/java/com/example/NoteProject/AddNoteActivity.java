package com.example.NoteProject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NoteProject.Activities.AudioActivity;
import com.example.NoteProject.Activities.MapsActivity;
import com.example.NoteProject.Adapters.MyRecyclerViewAdapter;
import com.example.NoteProject.Database.DBImage;
import com.example.NoteProject.Database.DBNote;
import com.example.NoteProject.Modals.Image;
import com.example.NoteProject.Modals.Note;
import com.example.NoteProject.Singelton.AudioSingleton;
import com.example.NoteProject.Singelton.SubjectSingleton;
import com.example.NoteProject.Utils.ShadowLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNoteActivity extends AppCompatActivity {
    private static final String IMAGE_DIRECTORY = "/dailynote";
    @BindView(R.id.save_tv)
    TextView saveTv;
    @BindView(R.id.save_sl)
    ShadowLayout saveSl;
    private int GALLERY = 1, CAMERA = 2;
    Button saveNote;
    EditText txtNoteTitle;
    EditText txtNoteContent;
    private static final String TAG = "AddNote";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    LatLng recentLatLng = null;

    double latitude;
    double longitude;

    boolean isEdit = false;
    DBImage dbImage = new DBImage(this);
    DBNote dbNote = new DBNote(this);

    Note noteIsEdit = new Note();
    Image imageIsEdit = new Image();

    private LinearLayout mGallery;
    private LayoutInflater mInflater;
    private HorizontalScrollView horizontalScrollView;
    private MyRecyclerViewAdapter adapter;

    RecyclerView recyclerView;
    ArrayList<Bitmap> mImgIds = new ArrayList<Bitmap>();

    DBNote dbSubject = new DBNote(this);

    Note myNoteObj = new Note();
    ArrayList<String> myImagesUrl = new ArrayList<String>();
    AudioSingleton audioSingleton = null;
    String audioUrl;
    String subjectName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_note);
      ButterKnife.bind(this);
      saveNote = findViewById(R.id.saveNote);
      txtNoteTitle = findViewById(R.id.txtNoteTitle);
      txtNoteContent = findViewById(R.id.txtNoteContent);

      recyclerView = findViewById(R.id.rvAnimals);

     getLocationPermission();
     getDeviceLocation();

      requestMultiplePermissions();

      Intent intent = this.getIntent();
        Bundle noteData  = intent.getExtras();
      subjectName = SubjectSingleton.getInstance().getSubjectName();

      if (noteData != null) {
        isEdit = noteData.getBoolean("isEdit");
        noteIsEdit = (Note) noteData.getSerializable("NoteData");
      }

      if (isEdit == true) {

        txtNoteTitle.setText(noteIsEdit.getNoteTitle());
        txtNoteContent.setText(noteIsEdit.getNoteContent());
        audioUrl = noteIsEdit.getAudio();
        recentLatLng = new LatLng(noteIsEdit.getLatitude(), noteIsEdit.getLongitude());
        if (noteIsEdit.getImage1() != null) {
          myImagesUrl.add(0, noteIsEdit.getImage1());

        }
        if (noteIsEdit.getImage2() != null) {
          myImagesUrl.add(0, noteIsEdit.getImage2());

        }
        if (noteIsEdit.getImage3() != null) {
          myImagesUrl.add(0, noteIsEdit.getImage3());

        }
          Log.d("test654","hjj" +noteIsEdit.getNoteId());


      }

      mInflater = LayoutInflater.from(this);
      setupRecyclerView();
      audioSingleton = AudioSingleton.getInstance();

    }

    private boolean checkNoteEntry() {
      if (txtNoteTitle.getText().toString().isEmpty()){
        Toast.makeText(this, "Please Enter Title ", Toast.LENGTH_SHORT).show();
        return false;
      }
      return true;
    }

    private void getImageUrl() {
      String imageURL;
      if (mImgIds.size() > 0) {
        for (int i = 0; i < mImgIds.size(); i++) {
          imageURL = saveImage(mImgIds.get(i));
          myImagesUrl.add(imageURL);

        }
      }
    }

    @Override
    public void onBackPressed() {
      super.onBackPressed();
    }

    public Note populateDataNote() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String strDate = sdf.format(new Date()); // pass date that get from database

      if (isEdit) {
        noteIsEdit.setSubjectName(subjectName);
        noteIsEdit.setNoteTitle(txtNoteTitle.getText().toString());
        noteIsEdit.setNoteContent(txtNoteContent.getText().toString());
        noteIsEdit.setAudio(audioSingleton.getAudioUrl());
        noteIsEdit.setDateTime(strDate);
        noteIsEdit.setLatitude(latitude);
        noteIsEdit.setLongitude(longitude);
        if (myImagesUrl.size() > 0) {
          if (myImagesUrl.size() >= 1) {
            noteIsEdit.setImage1(myImagesUrl.get(0));
          }
          if (myImagesUrl.size() >= 2) {
            noteIsEdit.setImage2(myImagesUrl.get(1));
          }
          if (myImagesUrl.size() >= 3) {
            noteIsEdit.setImage3(myImagesUrl.get(2));
          }
        }

        return noteIsEdit;

      } else {
        Note note = new Note();
        note.setSubjectName(subjectName);
        note.setNoteTitle(txtNoteTitle.getText().toString());
        note.setNoteContent(txtNoteContent.getText().toString());
        note.setAudio(audioSingleton.getAudioUrl());
        note.setDateTime(strDate);
        note.setLatitude(latitude);
        note.setLongitude(longitude);
        if (myImagesUrl.size() > 0) {
          if (myImagesUrl.size() == 1) {
            note.setImage1(myImagesUrl.get(0));
          }
          if (myImagesUrl.size() == 2) {
            note.setImage2(myImagesUrl.get(1));
          }
          if (myImagesUrl.size() == 3) {
            note.setImage3(myImagesUrl.get(2));
          }
        }
        return note;
      }
    }

    public void populateDataImage() {
      for (int j = 0; j < myImagesUrl.size(); j++) {
        Image image = new Image();
        image.setImageLocation(myImagesUrl.get(j));
        dbImage.insertImage(image);
      }
    }

    private void getLocationPermission() {
      String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.ACCESS_COARSE_LOCATION};

      if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
              FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
          mLocationPermissionsGranted = true;


        } else {
          ActivityCompat.requestPermissions(this,
                  permissions,
                  LOCATION_PERMISSION_REQUEST_CODE);
        }
      } else {
        ActivityCompat.requestPermissions(this,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE);
      }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      mLocationPermissionsGranted = false;
      switch (requestCode) {
        case LOCATION_PERMISSION_REQUEST_CODE: {
          if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
              if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = false;
                return;
              }
            }
            mLocationPermissionsGranted = true;
          }
        }
      }
    }

    private void getDeviceLocation() {
      mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddNoteActivity.this);
      try {
        if (mLocationPermissionsGranted) {
          final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
          location.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task task) {
              if (task.isSuccessful()) {
                Location currentLocation = (Location) task.getResult();
                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

              } else {
                Toast.makeText(AddNoteActivity.this, "Error in Location", Toast.LENGTH_SHORT).show();
              }
            }
          });
        }
      } catch (SecurityException e) {

      }
    }

    private void moveCamera(LatLng latLng) {
      latitude = latLng.latitude;
      longitude = latLng.longitude;
      recentLatLng = latLng;
    }


    private void setupRecyclerView() {
      ArrayList<String> noteImgNames = new ArrayList<>();
      if (!isEdit) {
        if (mImgIds.size() > 0) {
          for (int i = 0; i < mImgIds.size(); i++) {
            noteImgNames.add("");
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(AddNoteActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            adapter = new MyRecyclerViewAdapter(this, mImgIds, noteImgNames);
            recyclerView.setAdapter(adapter);
          }
        }
      } else {
        for (int i = 0; i < myImagesUrl.size(); i++) {
          mImgIds.clear();
          mImgIds.add(returnImageBitmap(myImagesUrl.get(i)));
        }

        if (mImgIds.size() > 0) {
          for (int i = 0; i < mImgIds.size(); i++) {
            noteImgNames.add("");
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(AddNoteActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            adapter = new MyRecyclerViewAdapter(this, mImgIds, noteImgNames);
            recyclerView.setAdapter(adapter);
          }
        }
      }
    }

    public Bitmap returnImageBitmap(String imgURL) {
      File imgFile = new File(imgURL);
      Bitmap myBitmap = null;

      if (imgFile.exists()) {
        myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
      }
      return myBitmap;
    }


    private void appendImgData(Bitmap img) {
      mImgIds.add(img);
    }


    /* Button clicks */
    public void galleryButtonClick(View view) {
      choosePhotoFromGallary();
    }

    public void audioButtonClick(View view) {
      Intent intent = new Intent(AddNoteActivity.this, AudioActivity.class);
      intent.putExtra("audiourl", audioUrl);
      intent.putExtra("isEdit", isEdit);
      startActivity(intent);
    }

    public void mapButtonClick(View view) {
      Intent intent = new Intent(AddNoteActivity.this, MapsActivity.class);
      if(noteIsEdit!=null){
        String noteID = String.valueOf(noteIsEdit.getNoteId());
        intent.putExtra("noteID",noteID);
         // intent.putExtra("noteID", noteIsEdit.getNoteId());
          intent.putExtra("isEdit", isEdit);
         // intent.putExtra("Latlng", recentLatLng);
      }
        intent.putExtra("Latlng", recentLatLng);


      startActivity(intent);
    }

    public void cameraButtonClick(View view) {
      takePhotoFromCamera();
    }


    public void choosePhotoFromGallary() {
      Intent galleryIntent = new Intent(Intent.ACTION_PICK,
              MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

      startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      startActivityForResult(intent, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      if (resultCode == this.RESULT_CANCELED) {
        return;
      }
      if (requestCode == GALLERY) {
        if (data != null) {
          Uri contentURI = data.getData();
          try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
            appendImgData(bitmap);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

      } else if (requestCode == CAMERA) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        appendImgData(thumbnail);
      }
      setupRecyclerView();
    }

    public String saveImage(Bitmap myBitmap) {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
      File wallpaperDirectory = new File(
              Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
      if (!wallpaperDirectory.exists()) {
        wallpaperDirectory.mkdirs();
      }

      try {
        File f = new File(wallpaperDirectory, Calendar.getInstance()
                .getTimeInMillis() + ".jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        MediaScannerConnection.scanFile(this,
                new String[]{f.getPath()},
                new String[]{"image/jpeg"}, null);
        fo.close();

        return f.getAbsolutePath();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      return "";
    }


    private void requestMultiplePermissions() {
      Dexter.withActivity(this).withPermissions(
              Manifest.permission.CAMERA,
              Manifest.permission.WRITE_EXTERNAL_STORAGE,
              Manifest.permission.READ_EXTERNAL_STORAGE
      ).withListener(new MultiplePermissionsListener() {
        @Override
        public void onPermissionsChecked(MultiplePermissionsReport report) {
          // check if all permissions are granted
          if (report.areAllPermissionsGranted()) {
          }
          if (report.isAnyPermissionPermanentlyDenied()) {
          }
        }

        @Override
        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
          token.continuePermissionRequest();
        }
      }).withErrorListener(new PermissionRequestErrorListener() {
        @Override
        public void onError(DexterError error) {
        }
      }).onSameThread()
              .check();
    }

    @OnClick(R.id.save_tv)
    public void onViewClicked() {
      if (isEdit) {
        if (checkNoteEntry()){
          getImageUrl();
          dbNote.updateNote(populateDataNote());
          Intent returnIntent = new Intent();
          setResult(Activity.RESULT_OK, returnIntent);
          finish();
        }

      } else {
        if (checkNoteEntry()){
          getImageUrl();
          dbNote.insertNote(populateDataNote());
          Intent returnIntent = new Intent();
          setResult(Activity.RESULT_OK, returnIntent);
          finish();
        }

      }
    }

  }