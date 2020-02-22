package com.example.harpreet.visitorguide;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.harpreet.visitorguide.UtilsFolder.GPStracker;
import com.example.harpreet.visitorguide.UtilsFolder.Server;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class listCheck extends AppCompatActivity  {

    private CircleImageView view;
    private double Lat;
    private double Long;
    private Uri img;
    private String placeName;
    private String itemName;
    private String reviews;
    private String category;
    private float rate;
    Spinner s;
    EditText place;
    EditText item;
    EditText lati;
    EditText longi;
    EditText rev;
    RatingBar rating;
    Button button;
    private FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseReference;

    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_check);
        Spinner dropdown = findViewById(R.id.spinner1);
        view = findViewById(R.id.addImage);
        s=findViewById(R.id.spinner1);
        place=findViewById(R.id.placeName);
        item=findViewById(R.id.itemName);
        rev=findViewById(R.id.reviews);
        rating=findViewById(R.id.ratingBar);
        button=findViewById(R.id.submit);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePic();
            }
        });
//create a list of items for the spinner.
        String[] items = new String[]{"Select Category", "Street Food", "Sweets", "Tea(other hot drinks)", "Shakes", "Bebinca", "Gadbad Ice-Cream", "Frankies", "Cuisines", "Handicrafts"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        Intent mIntent = getIntent();
        Lat = mIntent.getDoubleExtra("lat", Lat);
        Long = mIntent.getDoubleExtra("long", Long);
        lati=findViewById(R.id.latitude);
        longi=findViewById(R.id.longitude);
        String lon=String.format("%.2f", Long);
        String latd=String.format("%.2f", Lat);
        lati.setText("Latitude : "+latd);
        longi.setText("Longitude :"+lon);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                placeName=place.getText().toString();
//                itemName=item.getText().toString();
//                category=s.getSelectedItem().toString();
//                reviews=rev.getText().toString();
//                rate=rating.getRating();
//                locals l=new locals(Lat,Long,img,placeName,itemName,reviews,category,rate);
//                mDatabaseReference = mDatabase.getReference().child("localVendors");
//                mDatabaseReference.setValue(l);


            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                doSomethingWithCroppedImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void doSomethingWithCroppedImage(Uri outputUri) {
        view = findViewById(R.id.addImage);
        img=outputUri;
        view.setImageURI(outputUri);

//        final StorageReference image_path=storageReference.child("localVendors");//storing the image to storage on specified path
//
//        image_path.putFile(outputUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) { ;
//                        workOndetails(uri);
//                    }
//
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//
//                    }
//                });
//            }
//
//        });

    }


//    private void workOndetails(final Uri image) {
//
//        DatabaseReference mDatabase;
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
////                Server server = dataSnapshot.getValue(Server.class);
////                if(server==null)
////                    Toast.makeText(listCheck.this, "empty", Toast.LENGTH_SHORT).show();
////                else
////                {
////                    ModelURL = server.model;
////                    callToModel(image);
////                }
//                imageView.setImageURI(null);
//                imageView.setImageURI(image);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(listCheck.this, "Unable to read", Toast.LENGTH_SHORT).show();
//            }
//        };
//        mDatabase.addValueEventListener(postListener);
//
//
//
//
//    }

//    private void callToModel(Uri image) {
//
//        dialog.setTitle("Image Processing");
//        JSONObject obj = new JSONObject();
//        try {
////            Toast.makeText(this, image.toString(), Toast.LENGTH_SHORT).show();
//            obj.put("downloadUrl", image.toString());
//        }catch (Exception e)
//        {
//
//        }
////        Toast.makeText(this, ModelURL, Toast.LENGTH_SHORT).show();
//        AndroidNetworking.post("http://"+ModelURL)
//                .addJSONObjectBody(obj)
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsString(new StringRequestListener() {
//                    @Override
//                    public void onResponse(String response) {
//
//
//                        String data[] = response.split("'");
//                        String name = data[3];
//                        double prob = Double.parseDouble(data[6].substring(2,data[6].length()-3))*100;
//                        Toast.makeText(listCheck.this, ""+prob, Toast.LENGTH_SHORT).show();
//                        if(prob<45){
//                            dialog.dismiss();bar.setVisibility(View.INVISIBLE);
//                            desc.setText("Sorry Data related to this image is not available!");
//                        }
//                        else {
//                            dialog.setTitle("Fetching Data");
//                            callAtWikipage(name);
//
//                        }
//                    }
//                    @Override
//                    public void onError(ANError error) {
//                        Toast.makeText(listCheck.this, "Dinesh:"+error.toString(), Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();bar.setVisibility(View.INVISIBLE);
//                    }
//                });
//    }
//
//    private void callAtWikipage(final String name) {
//
//        AndroidNetworking.get("https://wikifun.herokuapp.com/info")
//                .setPriority(Priority.MEDIUM)
//                .addQueryParameter("location",name+" goa,India")
//                .build()
//                .getAsString(new StringRequestListener() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        desc.setText(name+" : "+response);
//                        //inc count of res place
//                        dialog.dismiss();
//
//                    }
//                    @Override
//                    public void onError(ANError error) {
//                        Toast.makeText(listCheck.this, "Ashu:"+error.toString(), Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();bar.setVisibility(View.INVISIBLE);
//                    }
//                });
//
//    }


    private void takePic()
    {

//        dialog = new ProgressDialog(this);
//        dialog.setMessage("Loading Data...");
//        dialog.setTitle("Image Uploading");
//        dialog.show();
        //bar.setVisibility(View.VISIBLE);

        storageReference= FirebaseStorage.getInstance().getReference(); //getting the reference of the storage
        //that is the path to storage on the server is saved here

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please Try Again!", Toast.LENGTH_LONG).show();
                //the toast message will appear for the deny for the first time but in below line the connection is established
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                CropImage.activity()
                        .setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON)
                        .start(this);
            }
        }
    }

    public void checkimage(View view) {

        takePic();

    }

}
